package plus.extvos.builtin.upload.controller;

import cn.hutool.core.io.IoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import plus.extvos.builtin.upload.config.FileManagerConfig;
import plus.extvos.builtin.upload.dto.FileInfo;
import plus.extvos.builtin.upload.dto.ResumableInfo;
import plus.extvos.builtin.upload.dto.UploadFile;
import plus.extvos.builtin.upload.dto.UploadOptions;
import plus.extvos.builtin.upload.enums.FileType;
import plus.extvos.builtin.upload.service.FileService;
import plus.extvos.builtin.upload.service.impl.ResultStorage;
import plus.extvos.builtin.upload.service.impl.ResumableInfoStorage;
import plus.extvos.common.Assert;
import plus.extvos.common.Result;
import plus.extvos.common.ResultCode;
import plus.extvos.common.exception.ResultException;
import plus.extvos.common.utils.QuickHash;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static plus.extvos.builtin.upload.enums.ResultCode.FILE_NOT_EXISTS;
import static plus.extvos.builtin.upload.enums.ResultCode.FORBIDDEN_READ;

/**
 * Abstract FileManager Controller, a basic filemanager with upload/delete/directory etc...
 *
 * @author shenmc
 */
@Api(tags = {"文件管理"})
public abstract class AbstractFileManagerController {
    private static final Logger log = LoggerFactory.getLogger(AbstractFileManagerController.class);
    private static final ResumableInfoStorage resumableInfoStorage = ResumableInfoStorage.getInstance();
    private static final ResultStorage uploadResultStorage = ResultStorage.getInstance();

    protected abstract FileService fs();

    @Autowired
    private FileManagerConfig fileManagerConfig;

    @GetMapping("/options")
    @ApiOperation(value = "选项配置", notes = "获取文件管理器的选项配置。 ")
    public Result<UploadOptions> getOptions() throws ResultException {
        UploadOptions opts = new UploadOptions(
                fileManagerConfig.getChunkSize(),
                fileManagerConfig.getSimultaneous(),
                fileManagerConfig.getTemporary(),
                fileManagerConfig.getBaseUrl(),
                fileManagerConfig.getPrefix(),
                fileManagerConfig.getRoot()
        );
        return Result.data(opts).success();
    }

    @PostMapping("/{bucket:[A-Za-z0-9_-]+}/files")
    @ApiOperation(value = "上传切片或文件或请求创建目录", notes = "上传切片或文件或请求创建目录。切片上传遵循Resumable.js的规格。Multipart方式文件上传亦支持。mkdir=true时指定创建目录。")
    public Result<FileInfo> doPostRequest(@PathVariable("bucket") String bucket,
                                          @RequestParam(value = "path", required = false) String path,
                                          @RequestParam(value = "filename", required = false) String filename,
                                          @RequestParam(value = "mkdir", required = false) Boolean mkdir,
                                          @RequestParam(value = "resumableChunkNumber", required = false) Integer resumableChunkNumber,
                                          @RequestParam(value = "resumableChunkSize", required = false) Long resumableChunkSize,
                                          @RequestParam(value = "resumableCurrentChunkSize", required = false) Long resumableCurrentChunkSize,
                                          @RequestParam(value = "resumableTotalSize", required = false) Long resumableTotalSize,
                                          @RequestParam(value = "resumableTotalChunks", required = false) Integer resumableTotalChunks,
                                          @RequestParam(value = "resumableType", required = false) String resumableType,
                                          @RequestParam(value = "resumableIdentifier", required = false) String resumableIdentifier,
                                          @RequestParam(value = "resumableFilename", required = false) String resumableFilename,
                                          @RequestParam(value = "resumableRelativePath", required = false) String resumableRelativePath,
                                          @RequestPart(required = false) MultipartFile file,
                                          @ApiParam(hidden = true) HttpServletRequest request
    ) throws ResultException {
        Assert.notEmpty(bucket, ResultException.forbidden("bucket can not be empty"));
        if (null != resumableIdentifier && null != resumableFilename) {
            ResumableInfo resumableInfo = new ResumableInfo();
            resumableInfo.chunkNum = resumableChunkNumber;
//            resumableInfo.chunkSize = resumableChunkSize;
            resumableInfo.chunkSize = resumableCurrentChunkSize;
            resumableInfo.totalChunks = resumableTotalChunks;
//            resumableInfo. = resumableType;
            resumableInfo.identifier = resumableIdentifier;
            resumableInfo.filename = resumableFilename;
            resumableInfo.relativePath = resumableRelativePath;
            FileInfo fi = uploadByResumable(bucket, path, resumableInfo, request);
            return Result.data(fi).success();
        } else if (null != mkdir && mkdir.equals(true)) {
            return Result.data(fs().mkdir(bucket, path, filename)).success(ResultCode.CREATED);
        } else if (null != file && !file.isEmpty()) {
            try {
                OutputStream os = fs().save(bucket, path, file.getOriginalFilename());
                InputStream is = file.getInputStream();
                long size = IoUtil.copy(is, os);
                os.close();
                is.close();
                return Result.data(fs().stat(bucket, path, file.getOriginalFilename(), true, false)).success();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw ResultException.badRequest("invalid request");
        }
        throw ResultException.notImplemented();
    }

    @GetMapping("/{bucket:[A-Za-z0-9_-]+}/raw")
    @ApiOperation(value = "读取文件内容", notes = "读取文件内容")
    public void getFileRaw(@PathVariable("bucket") String bucket,
                           @RequestParam(value = "path", required = false) String path,
                           @RequestParam(value = "filename", required = false) String filename,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {

        try {
            File f = fs().file(bucket, path, filename);
            if (!f.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "file not found");
                return;
            } else if (!f.isFile()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "file is not a regular file");
                return;
            }
            InputStream is = fs().read(bucket, path, filename);

            if (null == is) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "open file failed");
            } else {
                OutputStream os = response.getOutputStream();
                String contentType = Files.probeContentType(f.toPath());
                response.setContentType(contentType);
                response.setStatus(HttpServletResponse.SC_OK);
                int len = 0;
                byte[] bytes = new byte[1024 * 100];
                while ((len = is.read(bytes)) > 0) {
                    os.write(bytes, 0, len);
                }
                is.close();
                os.close();
            }

        } catch (ResultException e) {
            if (FORBIDDEN_READ.equals(e.getCode())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            } else if (FILE_NOT_EXISTS.equals(e.getCode())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @GetMapping("/{bucket:[A-Za-z0-9_-]+}/files")
    @ApiOperation(value = "获取切片、文件或目录信息", notes = "获取切片、文件或目录信息，切片信息遵循Resumable.js的规格，而文件或目录信息则提供path和filename即可。")
    public Result<FileInfo> doGetRequest(@PathVariable("bucket") String bucket,
                                         @RequestParam(value = "path", required = false) String path,
                                         @RequestParam(value = "filename", required = false) String filename,
                                         @RequestParam(value = "withMd5", required = false) Boolean withMd5,
                                         @RequestParam(value = "resumableChunkNumber", required = false) Integer resumableChunkNumber,
                                         @RequestParam(value = "resumableChunkSize", required = false) Long resumableChunkSize,
                                         @RequestParam(value = "resumableCurrentChunkSize", required = false) Long resumableCurrentChunkSize,
                                         @RequestParam(value = "resumableTotalSize", required = false) Long resumableTotalSize,
                                         @RequestParam(value = "resumableTotalChunks", required = false) Integer resumableTotalChunks,
                                         @RequestParam(value = "resumableType", required = false) String resumableType,
                                         @RequestParam(value = "resumableIdentifier", required = false) String resumableIdentifier,
                                         @RequestParam(value = "resumableFilename", required = false) String resumableFilename,
                                         @RequestParam(value = "resumableRelativePath", required = false) String resumableRelativePath
    ) throws ResultException {
        Assert.notEmpty(bucket, ResultException.forbidden("bucket can not be empty"));
        if (null != resumableIdentifier && null != resumableFilename) {
            FileInfo fi = null;
            try {
                fi = fs().stat(bucket, resumableRelativePath, resumableFilename, false, false);
            } catch (ResultException ignored) {
//                fi = null;
            }
            if (null == fi) {
                File f = fs().chunk(bucket, resumableRelativePath, resumableFilename, resumableChunkNumber);
                if (!f.exists()) {
                    throw ResultException.notFound();
                } else {
                    fi = new FileInfo(resumableRelativePath, resumableFilename, 0L, FileType.FILE, LocalDateTime.now(), "");
                    fi.setChunkNum(resumableChunkNumber);
                }
            }
            return Result.data(fi).success();

        } else {
            return Result.data(fs().stat(bucket, path, filename, withMd5, true)).success();
        }
    }

    @DeleteMapping("/{bucket:[A-Za-z0-9_-]+}/files")
    @ApiOperation(value = "删除文件或目录", notes = "删除文件或目录，recursive=true时支持删除非空目录。")
    public Result<FileInfo> doDeleteRequest(@PathVariable("bucket") String bucket,
                                            @RequestParam(value = "path", required = false) String path,
                                            @RequestParam(value = "filename", required = false) String filename,
                                            @RequestParam(value = "recursive", required = false) Boolean recursive
    ) throws ResultException {
        Assert.notEmpty(bucket, ResultException.forbidden("bucket can not be empty"));
        FileInfo fi = fs().stat(bucket, path, filename, false, false);
        switch (fi.getType()) {
            case FILE:
            case DIRECTORY:
            case SYMBOL:
                fs().delete(bucket, path, filename, recursive);
                break;
            default:
                throw ResultException.forbidden("can not delete");
        }
        return Result.data(fi).success();
    }

    private FileInfo uploadByResumable(String bucket, String path, ResumableInfo info, HttpServletRequest request) throws ResultException {
        log.debug("uploadByResumable:> category {}", bucket);
        log.debug("uploadByResumable:> info {}", info);
        Assert.isTrue(info.valid(), ResultException.badRequest("invalid resumable parameters"));
        UploadFile uploadFile = null; //new UploadFile(category, info.identifier, info.filename, processor().root(), processor().prefix(), info.totalSize, info.filename, "");

        long contentLength = request.getContentLength();
        if (contentLength != info.chunkSize) {
            log.error("uploadByResumable:> content-length not match chunk-size: {} {}", contentLength, info.chunkSize);
            throw ResultException.badRequest("content-length(" + contentLength + ") not match chunk-size(" + info.chunkSize + ")");
        }
        FileInfo fileInfo = new FileInfo(path, info.filename, info.chunkSize, FileType.FILE, LocalDateTime.now(), "");
        fileInfo.setChunkNum(info.chunkNum);
        try {
            OutputStream out = new FileOutputStream(fs().chunk(bucket, path, info.filename, info.chunkNum));
            InputStream is = request.getInputStream();
            long readed = 0;
            byte[] bytes = new byte[1024 * 100];
            while (readed < contentLength) {
                int r = is.read(bytes);
                if (r < 0) {
                    break;
                }
                out.write(bytes, 0, r);
                readed += r;
            }
            is.close();
            out.close();
            resumableInfoStorage.set(info);
            if (resumableInfoStorage.size(info.identifier) >= info.totalChunks) {
                fileInfo = mergeChunks(bucket, path, info.filename, info.identifier, info.totalChunks);
                resumableInfoStorage.remove(info.identifier);
                fileInfo.setChunkNum(null);
                fileInfo.setSize(info.totalSize);
            }
        } catch (IOException e) {
            log.error(">>", e);
            throw ResultException.internalServerError("read request failed: " + e.getMessage());
        }
        return fileInfo;
//        throw ResultException.notImplemented("not implemented yet!!!");
    }

    /**
     * Merge chunks into file
     *
     * @param bucket
     * @param path
     * @param filename
     * @param identifier
     * @param chunks
     * @return
     * @throws IOException
     */
    private FileInfo mergeChunks(String bucket, String path, String filename, String identifier, int chunks) throws IOException {
        List<String> chunkFiles = new ArrayList<String>();
        FileInfo fileInfo = new FileInfo(path, filename, 0L, FileType.FILE, null, ""); //
        log.debug("mergeSegments:> {}, {} ...", identifier, chunks);
        for (int i = 1; i <= chunks; i++) {
            ResumableInfo info = resumableInfoStorage.get(identifier, i);
            if (null == info) {
                log.warn("can not get chunk info: {}[{}]", identifier, i);
                throw ResultException.conflict("chunk missing:" + i);
            }
            chunkFiles.add(info.chunkFilename);
        }
        OutputStream out = fs().save(bucket, path, filename);
        QuickHash qh = QuickHash.md5();
        for (String fn : chunkFiles) {
            log.debug("Reading segment {} ...", fn);
            InputStream in = new FileInputStream(fn);
            int len = 0;
            byte[] bytes = new byte[1024 * 100];
            while ((len = in.read(bytes)) > 0) {
                out.write(bytes, 0, len);
                qh.update(bytes, 0, len);
                fileInfo.setSize(fileInfo.getSize() + len);
            }
            in.close();
        }
        out.close();
        fileInfo.setMd5(qh.hex());
        fileInfo.setModified(LocalDateTime.now());
        log.debug("merged segments of {} ", filename);
        return fileInfo;
        // TODO: remove segments
    }

}

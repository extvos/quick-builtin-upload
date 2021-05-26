package org.extvos.builtin.controller;

import cn.hutool.core.io.file.FileNameUtil;
import org.extvos.builtin.config.UploadConfig;
import org.extvos.builtin.entity.ResumableInfo;
import org.extvos.builtin.entity.UploadFile;
import org.extvos.builtin.service.StorageService;
import org.extvos.common.utils.QuickHash;
import org.extvos.restlet.RestletCode;
import org.extvos.restlet.Result;
import org.extvos.restlet.exception.RestletException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Mingcai SHEN
 */
public abstract class AbstractUploadController<T extends StorageService> {

    private static final Logger log = LoggerFactory.getLogger(AbstractUploadController.class);

    /**
     * get current configuration
     *
     * @return UploadConfig
     */
    abstract UploadConfig config();

    /**
     * get storage service
     *
     * @return the service
     */
    abstract T processor();

    private ResumableInfo buildResumableInfo(Map<String, String> queries) {
        ResumableInfo info = new ResumableInfo();
        if (null != queries) {
            info.chunkSize = Long.parseLong(queries.getOrDefault(config().getCurrentChunkSizeParameterName(), "-1"));
            info.totalSize = Long.parseLong(queries.getOrDefault(config().getTotalSizeParameterName(), "-1"));
            info.identifier = queries.getOrDefault(config().getIdentifierParameterName(), "");
            info.filename = queries.getOrDefault(config().getFileNameParameterName(), "");
            info.relativePath = queries.getOrDefault(config().getRelativePathParameterName(), "");
        }
        return info;
    }

    private int[] pathSegments;

    private int[] buildPathSegments() throws RestletException {
        if (pathSegments != null) {
            return pathSegments;
        }
        List<Integer> segs = new LinkedList<>();
        if (config().getPathSegments() < 2) {
            throw RestletException.internalServerError("upload config error: path-segments can not less than 2");
        }
        if (config().getPathSegments() > 8) {
            throw RestletException.internalServerError("upload config error: path-segments can not more than 8");
        }
        for (int i = 0; i < config().getPathSegments(); i++) {
            segs.add(i == 0 ? 2 : 4);
        }
        Integer[] ss = segs.toArray(new Integer[0]);
        pathSegments = new int[ss.length];
        for (int i = 0; i < ss.length; i++) {
            pathSegments[i] = ss[i];
        }
        return pathSegments;
    }

    private String buildTargetFilename(String category, String filename, String... refs) throws RestletException {
        String targetFilename;
        String[] ss = QuickHash.md5().hash(category + filename + String.join("", refs)).hexSegments(buildPathSegments());
        targetFilename = String.join("/", category, String.join("/", ss)) + "." + FileNameUtil.extName(filename);
        return targetFilename;
    }

    private OutputStream createFile(String filename) throws RestletException {
        File f = new File(filename);
        File pf = f.getParentFile();
        if (!pf.exists()) {
            if (!pf.mkdirs()) {
                throw RestletException.forbidden("create path '" + pf.getPath() + "' failed.");
            }
        }
        try {
            return new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RestletException(UploadResultCode.FORBIDDEN_CREATE,
                "create file '" + f.getPath() + "' failed: " + e.getMessage());
        }
    }

    /**
     * Handling upload single segment via MultipartFile
     *
     * @param category as category
     * @param file     as multipart file
     * @return uploaded file
     * @throws RestletException when errors
     */
    private UploadFile uploadByMultipartFile(String category, MultipartFile file) throws RestletException {
        /* When the upload is a single multi-part file */
        String root = processor().useTemporary() ? config().getTemporary() : config().getRoot();
        /* We generate a random identifier here to allow upload the same filename to same category.*/
        String identifier = QuickHash.md5().random().hex();
        String targetFilename = buildTargetFilename(category, file.getOriginalFilename(), file.getContentType(),
            identifier);
        UploadFile uploadFile = new UploadFile(
            targetFilename, root, config().getPrefix(), file.getSize(), "");
        String fullFilename = String.join("/", root, targetFilename);
        OutputStream out = createFile(fullFilename);
        try {
            byte[] bytes = file.getBytes();
            uploadFile.setChecksum(QuickHash.md5().hash(bytes).hex());
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestletException(UploadResultCode.FORBIDDEN_CREATE,
                "write file '" + targetFilename + "' failed: " + e.getMessage());
        }
        try {
            /* call processor to process uploaded file, remove it when return TRUE of got an exception */
            if (processor().process(root, config().getPrefix(), targetFilename, category,
                identifier, file.getOriginalFilename())) {
                if (!new File(fullFilename).delete()) {
                    log.warn("doFileUpload:> delete file {} failed", fullFilename);
                }
            }
        } catch (RestletException e) {
            if (!new File(fullFilename).delete()) {
                log.warn("doFileUpload:> delete file {} failed", fullFilename);
            }
            throw e;
        }
        return uploadFile;
    }

    /**
     * Handling upload via Resumable mode
     *
     * @param category as category
     * @param info     primary Resumable information
     * @param request  the original request
     * @return uploaded file
     * @throws RestletException when errors
     */
    private UploadFile uploadByResumable(String category, ResumableInfo info, HttpServletRequest request) throws RestletException {
        throw RestletException.notImplemented("not implemented yet!!!");
    }


    @ApiOperation(value = "文件上传", notes = "支持文件上传和切片上传。" +
        "请勿使用Swagger测试，访问 <a target='_blank' href='_builtin/upload-test/index.html'>Upload Test</a>")
    @PostMapping
    public Result<UploadFile> doFileUpload(
        @PathVariable("category") String category,
        @RequestParam(required = false) Map<String, String> queries,
        @RequestPart(required = false) MultipartFile file,
        @ApiParam(hidden = true) HttpServletRequest request) throws RestletException {
        ResumableInfo info = buildResumableInfo(queries);
        if (request.getContentLengthLong() < 1) {
            throw RestletException.badRequest("invalid request, request body can not be empty");
        }
        if (info.valid()) {
            UploadFile uploadFile = uploadByResumable(category, info, request);
            return Result.data(uploadFile).success();
        } else if (null != file && !file.isEmpty()) {
            UploadFile uploadFile = uploadByMultipartFile(category, file);
            return Result.data(uploadFile).success();
        } else {
            throw RestletException.badRequest("invalid request, neither segmented or full file");
        }
    }

    @ApiOperation(value = "上传检查", notes = "对切片上传的切片进行检查，减少重复上传")
    @GetMapping
    public Result<?> doUploadCheck(@PathVariable("category") String category,
                                   @RequestParam(required = false) Map<String, String> queries) throws RestletException {
        ResumableInfo info = buildResumableInfo(queries);
        if (!info.valid()) {
            throw RestletException.badRequest("only segmenting is allowed");
        }

        String fullFilename = buildTargetFilename(
            processor().useTemporary() ? config().getTemporary() : config().getRoot(), category, info.filename, info.identifier);
        if (processor().exists(fullFilename, info.identifier)) {
            return Result.data(info).success();
        } else {
            return Result.message("file not exists").failure(RestletCode.NOT_FOUND);
        }
    }
}

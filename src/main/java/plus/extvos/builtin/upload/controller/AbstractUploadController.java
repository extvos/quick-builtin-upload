package plus.extvos.builtin.upload.controller;

import cn.hutool.core.io.file.FileNameUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import plus.extvos.builtin.upload.entity.ResumableInfo;
import plus.extvos.builtin.upload.entity.UploadFile;
import plus.extvos.builtin.upload.entity.UploadResult;
import plus.extvos.builtin.upload.service.StorageService;
import plus.extvos.common.utils.QuickHash;
import plus.extvos.common.ResultCode;
import plus.extvos.common.Result;
import plus.extvos.common.exception.ResultException;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Mingcai SHEN
 */
public abstract class AbstractUploadController {

    private static final Logger log = LoggerFactory.getLogger(AbstractUploadController.class);

    /**
     * get storage service
     *
     * @return the service
     */
    protected abstract StorageService processor();

    private ResumableInfo buildResumableInfo(Map<String, String> queries) {
        ResumableInfo info = new ResumableInfo();
        if (null != queries) {
            info.chunkSize = Long.parseLong(queries.getOrDefault(processor().currentChunkSizeParameterName(), "-1"));
            info.totalSize = Long.parseLong(queries.getOrDefault(processor().totalSizeParameterName(), "-1"));
            info.identifier = queries.getOrDefault(processor().identifierParameterName(), "");
            info.filename = queries.getOrDefault(processor().fileNameParameterName(), "");
            info.relativePath = queries.getOrDefault(processor().relativePathParameterName(), "");
        }
        return info;
    }

    private int[] pathSegments;

    private int[] buildPathSegments() throws ResultException {
        if (pathSegments != null) {
            return pathSegments;
        }
        List<Integer> segs = new LinkedList<>();
        if (processor().pathSegments() < 2) {
            throw ResultException.internalServerError("upload config error: path-segments can not less than 2");
        }
        if (processor().pathSegments() > 8) {
            throw ResultException.internalServerError("upload config error: path-segments can not more than 8");
        }
        for (int i = 0; i < processor().pathSegments(); i++) {
            segs.add(i == 0 ? 2 : 4);
        }
        Integer[] ss = segs.toArray(new Integer[0]);
        pathSegments = new int[ss.length];
        for (int i = 0; i < ss.length; i++) {
            pathSegments[i] = ss[i];
        }
        return pathSegments;
    }

    private String buildTargetFilename(String category, String filename, String... refs) throws ResultException {
        String targetFilename;
        String[] ss = QuickHash.md5().hash(category + filename + String.join("", refs)).hexSegments(buildPathSegments());
        targetFilename = String.join("/", category, String.join("/", ss)) + "." + FileNameUtil.extName(filename);
        return targetFilename;
    }

    private OutputStream createFile(String filename) throws ResultException {
        File f = new File(filename);
        File pf = f.getParentFile();
        if (!pf.exists()) {
            if (!pf.mkdirs()) {
                throw ResultException.forbidden("create path '" + pf.getPath() + "' failed.");
            }
        }
        try {
            return new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ResultException(UploadResultCode.FORBIDDEN_CREATE,
                "create file '" + f.getPath() + "' failed: " + e.getMessage());
        }
    }

    /**
     * Handling upload single segment via MultipartFile
     *
     * @param category as category
     * @param file     as multipart file
     * @return uploaded file
     * @throws ResultException when errors
     */
    private Object uploadByMultipartFile(String category, Map<String, String> queries, MultipartFile file) throws ResultException {
        /* When the upload is a single multi-part file */
        String root = processor().useTemporary() ? processor().temporary() : processor().root();
        /* We generate a random identifier here to allow upload the same filename to same category.*/
        String identifier = QuickHash.md5().random().hex();
        String targetFilename = buildTargetFilename(category, file.getOriginalFilename(), file.getContentType(),
            identifier);
        UploadFile uploadFile = new UploadFile(identifier, targetFilename, root, processor().prefix(), file.getSize(), file.getOriginalFilename(), "");
        String fullFilename = String.join("/", root, targetFilename);
        OutputStream out = createFile(fullFilename);
        try {
            byte[] bytes = file.getBytes();
            uploadFile.setChecksum(QuickHash.md5().hash(bytes).hex());
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResultException(UploadResultCode.FORBIDDEN_CREATE,
                "write file '" + targetFilename + "' failed: " + e.getMessage());
        }
        try {
            /* call processor to process uploaded file, remove it when return TRUE of got an exception */
            UploadResult result = processor().process(uploadFile, category, queries);
            if (result.isProcessed()) {
                if (!new File(fullFilename).delete()) {
                    log.warn("doFileUpload:> delete file {} failed", fullFilename);
                }
            }
            if (result.getResult() != null) {
                return result.getResult();
            }
        } catch (ResultException e) {
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
     * @throws ResultException when errors
     */
    private Object uploadByResumable(String category, ResumableInfo info, Map<String, String> queries, HttpServletRequest request) throws ResultException {
        throw ResultException.notImplemented("not implemented yet!!!");
    }


    @ApiOperation(value = "文件上传", notes = "支持文件上传和切片上传。" +
        "请勿使用Swagger测试，访问 <a target='_blank' href='_builtin/upload-test/index.html'>Upload Test</a>")
    @PostMapping("/{category:[A-Za-z0-9_-]+}")
    public Result<Object> doFileUpload(
        @PathVariable("category") String category,
        @RequestParam(required = false) Map<String, String> queries,
        @RequestPart(required = false) MultipartFile file,
        @ApiParam(hidden = true) HttpServletRequest request) throws ResultException {
        ResumableInfo info = buildResumableInfo(queries);
        if (request.getContentLengthLong() < 1) {
            throw ResultException.badRequest("invalid request, request body can not be empty");
        }
        if (info.valid()) {
            Object uploadFile = uploadByResumable(category, info, queries, request);
            return Result.data(uploadFile).success();
        } else if (null != file && !file.isEmpty()) {
            Object uploadFile = uploadByMultipartFile(category, queries, file);
            return Result.data(uploadFile).success();
        } else {
            throw ResultException.badRequest("invalid request, neither segmented or full file");
        }
    }

    @ApiOperation(value = "上传检查", notes = "对切片上传的切片进行检查，减少重复上传")
    @GetMapping("/{category:[A-Za-z0-9_-]+}")
    public Result<?> doUploadCheck(@PathVariable("category") String category,
                                   @RequestParam(required = false) Map<String, String> queries) throws ResultException {
        ResumableInfo info = buildResumableInfo(queries);
        if (!info.valid()) {
            throw ResultException.badRequest("only segmenting is allowed");
        }

        String fullFilename = buildTargetFilename(
            processor().useTemporary() ? processor().temporary() : processor().root(), category, info.filename, info.identifier);
        if (processor().exists(fullFilename, info.identifier)) {
            return Result.data(info).success();
        } else {
            return Result.message("file not exists").failure(ResultCode.NOT_FOUND);
        }
    }

    @ApiOperation(value = "文件上传选项", notes = "获取当前后台文件上传配置")
    @GetMapping("/options")
    public Result<Map<String, Object>> getUploadOptions() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("root", processor().root());
        m.put("prefix", processor().prefix());
        m.put("temporary", processor().temporary());
        m.put("chunkSize", processor().chunkSize());
        m.put("simultaneous", processor().simultaneous());
        m.put("chunkNumberParameterName", processor().chunkNumberParameterName());
        m.put("chunkSizeParameterName", processor().chunkSizeParameterName());
        m.put("currentChunkSizeParameterName", processor().currentChunkSizeParameterName());
        m.put("totalSizeParameterName", processor().totalSizeParameterName());
        m.put("typeParameterName", processor().typeParameterName());
        m.put("identifierParameterName", processor().identifierParameterName());
        m.put("fileNameParameterName", processor().fileNameParameterName());
        m.put("relativePathParameterName", processor().relativePathParameterName());
        m.put("totalChunksParameterName", processor().totalChunksParameterName());
        m.put("pathSegments", processor().pathSegments());
        return Result.data(m).success();
    }
}

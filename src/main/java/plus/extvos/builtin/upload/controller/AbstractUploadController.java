package plus.extvos.builtin.upload.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import plus.extvos.builtin.upload.entity.ResumableInfo;
import plus.extvos.builtin.upload.entity.UploadFile;
import plus.extvos.builtin.upload.entity.UploadResult;
import plus.extvos.builtin.upload.service.StorageService;
import plus.extvos.builtin.upload.service.impl.ResumableInfoStorage;
import plus.extvos.common.utils.QuickHash;
import plus.extvos.common.ResultCode;
import plus.extvos.common.Result;
import plus.extvos.common.exception.ResultException;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * @author Mingcai SHEN
 */
public abstract class AbstractUploadController {

    private static final Logger log = LoggerFactory.getLogger(AbstractUploadController.class);

    private static final ResumableInfoStorage resumbleInfoStorage = ResumableInfoStorage.getInstance();

    /**
     * get storage service
     *
     * @return the service
     */
    protected abstract StorageService processor();

    private ResumableInfo buildResumableInfo(Map<String, String> queries, String... categories) {
        ResumableInfo info = new ResumableInfo();
        if (null != queries) {
            info.chunkSize = Long.parseLong(queries.getOrDefault(processor().currentChunkSizeParameterName(), "-1"));
            info.totalSize = Long.parseLong(queries.getOrDefault(processor().totalSizeParameterName(), "-1"));
            info.identifier = queries.getOrDefault(processor().identifierParameterName(), "");
            info.filename = queries.getOrDefault(processor().fileNameParameterName(), "");
            info.relativePath = queries.getOrDefault(processor().relativePathParameterName(), "");
            info.totalChunks = Integer.parseInt(queries.getOrDefault(processor().totalChunksParameterName(), "0"));
            info.chunkNum = Integer.parseInt(queries.getOrDefault(processor().chunkNumberParameterName(), "0"));
            if (categories.length > 0) {
                info.fullFilename = buildTargetFilename(
                        String.join("/", processor().useTemporary() ? processor().temporary() : processor().root(), String.join("/", categories)), info.filename, info.identifier);
                info.chunkFilename = String.join("/",
                        processor().temporary(), String.join("/", categories), QuickHash.md5().hash(info.fullFilename).hex(),
                        "segment." + info.chunkNum);
            } else {
                info.fullFilename = buildTargetFilename(
                        processor().useTemporary() ? processor().temporary() : processor().root(), info.filename, info.identifier);
                info.chunkFilename = String.join("/",
                        processor().temporary(), QuickHash.md5().hash(info.fullFilename).hex(),
                        "segment." + info.chunkNum);
            }

        }
        ObjectMapper om = new ObjectMapper();
        try {
            log.debug("buildResumableInfo: {}", om.writeValueAsString(info));
        } catch (JsonProcessingException e) {

            log.error(">>", e);
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

    private OutputStream createFileStream(String filename) throws ResultException {
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

            log.error(">>", e);
            throw new ResultException(UploadResultCode.FORBIDDEN_CREATE,
                    "create file '" + f.getPath() + "' failed: " + e.getMessage());
        }
    }

    private Writer createFileWriter(String filename) throws ResultException {
        File f = new File(filename);
        File pf = f.getParentFile();
        if (!pf.exists()) {
            if (!pf.mkdirs()) {
                throw ResultException.forbidden("create path '" + pf.getPath() + "' failed.");
            }
        }
        try {
            return new FileWriter(f);
        } catch (IOException e) {

            log.error(">>", e);
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
    private UploadFile uploadByMultipartFile(String category, Map<String, String> queries, MultipartFile file) throws ResultException {
        /* When the upload is a single multi-part file */
        String root = processor().useTemporary() ? processor().temporary() : processor().root();
        /* We generate a random identifier here to allow upload the same filename to same category.*/
        String identifier = QuickHash.md5().random().hex();
        String targetFilename = buildTargetFilename(category, file.getOriginalFilename(), file.getContentType(),
                identifier);
        UploadFile uploadFile = new UploadFile(identifier, targetFilename, root, processor().prefix(), file.getSize(), file.getOriginalFilename(), "");
        String fullFilename = String.join("/", root, targetFilename);
        OutputStream out = createFileStream(fullFilename);
        try {
            byte[] bytes = file.getBytes();
            uploadFile.setChecksum(QuickHash.md5().hash(bytes).hex());
            out.write(bytes);
            out.close();
        } catch (IOException e) {

            log.error(">>", e);
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

    private UploadFile mergeSegments(String identifier, int chunks) throws IOException {
        String fullFilename = null;
        File pf = null;
        List<String> chunkFiles = new ArrayList<String>();
        UploadFile uploadFile = new UploadFile();
        log.debug("mergeSegments:> {}, {} ...", identifier, chunks);
        for (int i = 1; i <= chunks; i++) {
            ResumableInfo info = resumbleInfoStorage.get(identifier, i);
            if (null == info) {
                log.warn("can not get chunk info: {}[{}]", identifier, i);
                return null;
            }
            if (null == fullFilename) {
                fullFilename = info.fullFilename;
                String fname = processor().useTemporary() ? fullFilename.substring(processor().temporary().length()) : fullFilename.substring(processor().root().length());
                uploadFile.setFilename(fname);
                uploadFile.setSize(info.totalSize);
                uploadFile.setIdentifier(info.identifier);
                uploadFile.setOriginalName(info.filename);
                uploadFile.setUrl(processor().prefix() + "/" + fname);
                uploadFile.setRoot(processor().root());
                uploadFile.setPrefix(processor().prefix());
            }
            if (null == pf) {
                pf = new File(info.chunkFilename).getParentFile();
            }
            chunkFiles.add(info.chunkFilename);
        }
        OutputStream out = createFileStream(fullFilename);
        QuickHash qh = QuickHash.md5();
        for (String filename : chunkFiles) {
            log.debug("Reading segment {} ...", filename);
            InputStream in = new FileInputStream(filename);
            int len = 0;
            byte[] bytes = new byte[1024 * 100];
            while ((len = in.read(bytes)) > 0) {
                out.write(bytes, 0, len);
                qh.update(bytes, 0, len);
            }
            in.close();
        }
        out.close();
        if (null != pf) {
            FileUtils.deleteDirectory(pf);
        }
        uploadFile.setChecksum(qh.hex());
        log.debug("merged segments of {} ", fullFilename);
        return uploadFile;
        // TODO: remove segments
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
    private UploadFile uploadByResumable(String category, ResumableInfo info, Map<String, String> queries, HttpServletRequest request) throws ResultException {
        log.debug("uploadByResumable:> category {}", category);
        log.debug("uploadByResumable:> info {}", info);
        log.debug("uploadByResumable:> queries {}", queries);
        if (!info.valid()) {
            throw ResultException.badRequest("invalid resumble parameters");
        }
        UploadFile uploadFile = new UploadFile();
        long contentLength = request.getContentLength();
        if (contentLength != info.chunkSize) {
            log.warn("uploadByResumable:> content-length not match chunk-size: {} {}", contentLength, info.chunkSize);
        }
        try {
            OutputStream out = createFileStream(info.chunkFilename);
            //Save to file
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
            resumbleInfoStorage.set(info);
            if (resumbleInfoStorage.size(info.identifier) >= info.totalChunks) {
                uploadFile = mergeSegments(info.identifier, info.totalChunks);
                resumbleInfoStorage.remove(info.identifier);
            }
        } catch (IOException e) {
            log.error(">>", e);
            throw ResultException.internalServerError("read request failed: " + e.getMessage());
        }
        return uploadFile;
//        throw ResultException.notImplemented("not implemented yet!!!");
    }


    @ApiOperation(value = "文件上传", notes = "支持文件上传和切片上传。" +
            "请勿使用Swagger测试，访问 <a target='_blank' href='_builtin/upload-test/index.html'>Upload Test</a>")
    @PostMapping("/{category:[A-Za-z0-9_-]+}")
    public Result<UploadFile> doFileUpload(
            @PathVariable("category") String category,
            @RequestParam(required = false) Map<String, String> queries,
            @RequestPart(required = false) MultipartFile file,
            @ApiParam(hidden = true) HttpServletRequest request) throws ResultException {
        ResumableInfo info = buildResumableInfo(queries, category);
        if (request.getContentLengthLong() < 1) {
            throw ResultException.badRequest("invalid request, request body can not be empty");
        }
        if (info.valid()) {
            UploadFile uploadFile = uploadByResumable(category, info, queries, request);
            return Result.data(uploadFile).success();
        } else if (null != file && !file.isEmpty()) {
            UploadFile uploadFile = uploadByMultipartFile(category, queries, file);
            return Result.data(uploadFile).success();
        } else {
            throw ResultException.badRequest("invalid request, neither segmented or full file");
        }
    }

    @ApiOperation(value = "上传检查", notes = "对切片上传的切片进行检查，减少重复上传")
    @GetMapping("/{category:[A-Za-z0-9_-]+}")
    public Result<UploadFile> doUploadCheck(@PathVariable("category") String category,
                                   @RequestParam(required = false) Map<String, String> queries) throws ResultException {
        ResumableInfo info = buildResumableInfo(queries, category);
        if (!info.valid()) {
            throw ResultException.badRequest("only segmenting is allowed");
        }
        UploadFile uploadFile = new UploadFile();

        if (processor().exists(info.fullFilename, info.identifier)) {
            return Result.data(uploadFile).success();
        } else {
            if (processor().exists(info.chunkFilename, info.identifier)) {
                resumbleInfoStorage.set(info);
                if (resumbleInfoStorage.size(info.identifier) >= info.totalChunks) {
                    try {
                        mergeSegments(info.identifier, info.totalChunks);
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error(">>", e);
                    }
                }
                return Result.data(uploadFile).success();
            }
            throw ResultException.notFound("file not exists");
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

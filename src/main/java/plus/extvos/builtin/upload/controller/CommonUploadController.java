package plus.extvos.builtin.upload.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plus.extvos.builtin.upload.config.UploadConfig;
import plus.extvos.builtin.upload.dto.UploadFile;
import plus.extvos.builtin.upload.dto.UploadResult;
import plus.extvos.builtin.upload.service.StorageService;
import plus.extvos.common.exception.ResultException;

import java.io.File;
import java.util.Map;

/**
 * @author Mingcai SHEN
 */
@RestController
@Api(tags = {"文件上传服务"})
@RequestMapping("/_builtin/upload")
@ConditionalOnProperty(prefix = "quick.builtin.upload", name = "enabled", havingValue = "true")
public class CommonUploadController extends AbstractUploadController implements StorageService {

    @Autowired
    private UploadConfig uploadConfig;

    static class CommonUploadFile extends UploadFile {
        private String ref;

        public CommonUploadFile() {

        }

        public CommonUploadFile(UploadFile uf) {
            // String identifier, String filename, String root, String prefix, long size, String origName, String checksum
            super(uf.getCategory(), uf.getIdentifier(), uf.getFilename(), uf.getRoot(), uf.getPrefix(), uf.getSize(), uf.getOriginalName(), uf.getChecksum());
            this.setType(uf.getType());
            this.ref = "Ref";
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }

    @Override
    protected StorageService processor() {
        return this;
    }

    @Override
    public UploadResult process(UploadFile uploadFile,
                                String category,
                                Map<String, String> queries) throws ResultException {
        return new UploadResult(new CommonUploadFile(uploadFile), false);
    }

    @Override
    public boolean exists(String filename, String identifier) {
        return new File(filename).exists();
    }

    @Override
    public boolean useTemporary() {
        return false;
    }

    @Override
    public String root() {
        return uploadConfig.getRoot();
    }

    @Override
    public String prefix() {
        return uploadConfig.getPrefix();
    }

    @Override
    public String temporary() {
        return uploadConfig.getTemporary();
    }

    /**
     * get chunkSize when upload in chunks
     *
     * @return bytes of chunk
     */
    @Override
    public long chunkSize() {
        return uploadConfig.getChunkSize();
    }

    /**
     * get simultaneous task of upload
     *
     * @return num
     */
    @Override
    public int simultaneous() {
        return uploadConfig.getSimultaneous();
    }

    /**
     * chunkNumberParameterName
     *
     * @return resumableChunkNumber
     */
    @Override
    public String chunkNumberParameterName() {
        return uploadConfig.getChunkNumberParameterName();
    }

    /**
     * chunkSizeParameterName
     *
     * @return resumableChunkSize
     */
    @Override
    public String chunkSizeParameterName() {
        return uploadConfig.getChunkSizeParameterName();
    }

    /**
     * currentChunkSizeParameterName
     *
     * @return resumableCurrentChunkSize
     */
    @Override
    public String currentChunkSizeParameterName() {
        return uploadConfig.getCurrentChunkSizeParameterName();
    }

    /**
     * totalSizeParameterName
     *
     * @return resumableTotalSize
     */
    @Override
    public String totalSizeParameterName() {
        return uploadConfig.getTotalSizeParameterName();
    }

    /**
     * typeParameterName
     *
     * @return resumableType
     */
    @Override
    public String typeParameterName() {
        return uploadConfig.getTypeParameterName();
    }

    /**
     * identifierParameterName
     *
     * @return resumableIdentifier
     */
    @Override
    public String identifierParameterName() {
        return uploadConfig.getIdentifierParameterName();
    }

    /**
     * fileNameParameterName
     *
     * @return resumableFilename
     */
    @Override
    public String fileNameParameterName() {
        return uploadConfig.getFileNameParameterName();
    }

    /**
     * relativePathParameterName
     *
     * @return resumableRelativePath
     */
    @Override
    public String relativePathParameterName() {
        return uploadConfig.getRelativePathParameterName();
    }

    /**
     * totalChunksParameterName
     *
     * @return resumableTotalChunks
     */
    @Override
    public String totalChunksParameterName() {
        return uploadConfig.getTotalChunksParameterName();
    }

    /**
     * pathSegments
     *
     * @return 4
     */
    @Override
    public int pathSegments() {
        return uploadConfig.getPathSegments();
    }
}

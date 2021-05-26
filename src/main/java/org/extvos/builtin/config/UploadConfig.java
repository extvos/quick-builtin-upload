package org.extvos.builtin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * @author shenmc
 */
@Configuration
public class UploadConfig {
    /**
     * 上传根目录
     */
    @Value("${quick.builtin.upload.root:/upload}")
    private String root;
    /**
     * 上传根目录
     */
    @Value("${quick.builtin.upload.prefix:/upload}")
    private String prefix;
    /**
     * 临时目录
     */
    @Value("${quick.builtin.upload.temporary:/tmp}")
    private String temporary;
    /**
     * 单片大小
     */
    @Value("${quick.builtin.upload.chunk-size:2097152}")
    private long chunkSize;
    /**
     * 每次提交片数
     */
    @Value("${quick.builtin.upload.simultaneous:5}")
    private Integer simultaneous;

    /**
     * 路径分段数量
     */
    @Value("${quick.builtin.upload.path-segments:3}")
    private Integer pathSegments;

    /**
     * 以下为切片时的Query参数名称配置
     */
    @Value("${quick.builtin.upload.parameters.chunk-number:resumableChunkNumber}")
    private String chunkNumberParameterName;

    @Value("${quick.builtin.upload.parameters.chunk-size:resumableChunkSize}")
    private String chunkSizeParameterName;

    @Value("${quick.builtin.upload.parameters.current-chunk-size:resumableCurrentChunkSize}")
    private String currentChunkSizeParameterName;

    @Value("${quick.builtin.upload.parameters.total-size:resumableTotalSize}")
    private String totalSizeParameterName;

    @Value("${quick.builtin.upload.parameters.type:resumableType}")
    private String typeParameterName;

    @Value("${quick.builtin.upload.parameters.identifier:resumableIdentifier}")
    private String identifierParameterName;

    @Value("${quick.builtin.upload.parameters.filename:resumableFilename}")
    private String fileNameParameterName;

    @Value("${quick.builtin.upload.parameters.relative-path:resumableRelativePath}")
    private String relativePathParameterName;

    @Value("${quick.builtin.upload.parameters.total-chunks:resumableTotalChunks}")
    private String totalChunksParameterName;

    public String getRoot() {
        return root;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTemporary() {
        return temporary;
    }

    public Long getChunkSize() {
        return chunkSize;
    }

    public Integer getSimultaneous() {
        return simultaneous;
    }

    public String getChunkNumberParameterName() {
        return chunkNumberParameterName;
    }

    public String getChunkSizeParameterName() {
        return chunkSizeParameterName;
    }

    public String getCurrentChunkSizeParameterName() {
        return currentChunkSizeParameterName;
    }

    public String getTotalSizeParameterName() {
        return totalSizeParameterName;
    }

    public String getTypeParameterName() {
        return typeParameterName;
    }

    public String getIdentifierParameterName() {
        return identifierParameterName;
    }

    public String getFileNameParameterName() {
        return fileNameParameterName;
    }

    public String getRelativePathParameterName() {
        return relativePathParameterName;
    }

    public String getTotalChunksParameterName() {
        return totalChunksParameterName;
    }

    public Integer getPathSegments() {
        return pathSegments;
    }
}

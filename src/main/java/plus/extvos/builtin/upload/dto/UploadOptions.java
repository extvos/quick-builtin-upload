package plus.extvos.builtin.upload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class UploadOptions implements Serializable {
    private long chunkSize;
    private int simultaneous;
    private String temporary;
    private String baseUrl;
    private String prefix;
    private String root;

    @JsonInclude(NON_NULL)
    private String chunkNumberParameterName;
    @JsonInclude(NON_NULL)
    private String chunkSizeParameterName;
    @JsonInclude(NON_NULL)
    private String currentChunkSizeParameterName;
    @JsonInclude(NON_NULL)
    private String totalSizeParameterName;
    @JsonInclude(NON_NULL)
    private String typeParameterName;
    @JsonInclude(NON_NULL)
    private String identifierParameterName;
    @JsonInclude(NON_NULL)
    private String fileNameParameterName;
    @JsonInclude(NON_NULL)
    private String relativePathParameterName;
    @JsonInclude(NON_NULL)
    private String totalChunksParameterName;
    @JsonInclude(NON_NULL)
    private Integer pathSegments;

    public UploadOptions() {
    }

    public UploadOptions(long chunkSize, int simultaneous, String temporary, String baseUrl, String prefix, String root) {
        this.chunkSize = chunkSize;
        this.simultaneous = simultaneous;
        this.temporary = temporary;
        this.baseUrl = baseUrl;
        this.prefix = prefix;
        this.root = root;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getSimultaneous() {
        return simultaneous;
    }

    public void setSimultaneous(int simultaneous) {
        this.simultaneous = simultaneous;
    }

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getChunkNumberParameterName() {
        return chunkNumberParameterName;
    }

    public void setChunkNumberParameterName(String chunkNumberParameterName) {
        this.chunkNumberParameterName = chunkNumberParameterName;
    }

    public String getChunkSizeParameterName() {
        return chunkSizeParameterName;
    }

    public void setChunkSizeParameterName(String chunkSizeParameterName) {
        this.chunkSizeParameterName = chunkSizeParameterName;
    }

    public String getCurrentChunkSizeParameterName() {
        return currentChunkSizeParameterName;
    }

    public void setCurrentChunkSizeParameterName(String currentChunkSizeParameterName) {
        this.currentChunkSizeParameterName = currentChunkSizeParameterName;
    }

    public String getTotalSizeParameterName() {
        return totalSizeParameterName;
    }

    public void setTotalSizeParameterName(String totalSizeParameterName) {
        this.totalSizeParameterName = totalSizeParameterName;
    }

    public String getTypeParameterName() {
        return typeParameterName;
    }

    public void setTypeParameterName(String typeParameterName) {
        this.typeParameterName = typeParameterName;
    }

    public String getIdentifierParameterName() {
        return identifierParameterName;
    }

    public void setIdentifierParameterName(String identifierParameterName) {
        this.identifierParameterName = identifierParameterName;
    }

    public String getFileNameParameterName() {
        return fileNameParameterName;
    }

    public void setFileNameParameterName(String fileNameParameterName) {
        this.fileNameParameterName = fileNameParameterName;
    }

    public String getRelativePathParameterName() {
        return relativePathParameterName;
    }

    public void setRelativePathParameterName(String relativePathParameterName) {
        this.relativePathParameterName = relativePathParameterName;
    }

    public String getTotalChunksParameterName() {
        return totalChunksParameterName;
    }

    public void setTotalChunksParameterName(String totalChunksParameterName) {
        this.totalChunksParameterName = totalChunksParameterName;
    }

    public Integer getPathSegments() {
        return pathSegments;
    }

    public void setPathSegments(Integer pathSegments) {
        this.pathSegments = pathSegments;
    }
}

package plus.extvos.builtin.upload.dto;

import java.io.Serializable;

public class UploadOptions implements Serializable {
    private long chunkSize;
    private int simultaneous;
    private String temporary;
    private String baseUrl;
    private String prefix;
    private String root;

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
}

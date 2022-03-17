package plus.extvos.builtin.upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileManagerConfig {
    /**
     * 上传根目录
     */
    @Value("${quick.builtin.filemanager.root:/tmp/upload}")
    private String root;
    /**
     * URI前缀
     */
    @Value("${quick.builtin.filemanager.prefix:/upload}")
    private String prefix;

    /**
     * 基础URL，用于外部静态访问
     */
    @Value("${quick.builtin.filemanager.base-url:}")
    private String baseUrl;
    /**
     * 临时目录
     */
    @Value("${quick.builtin.filemanager.temporary:/tmp/temp}")
    private String temporary;
    /**
     * 单片大小
     */
    @Value("${quick.builtin.filemanager.chunk-size:2097152}")
    private long chunkSize;
    /**
     * 每次提交片数
     */
    @Value("${quick.builtin.filemanager.simultaneous:5}")
    private Integer simultaneous;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Integer getSimultaneous() {
        return simultaneous;
    }

    public void setSimultaneous(Integer simultaneous) {
        this.simultaneous = simultaneous;
    }
}

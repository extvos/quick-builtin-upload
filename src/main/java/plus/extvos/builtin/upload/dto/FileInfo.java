package plus.extvos.builtin.upload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import plus.extvos.builtin.upload.enums.FileType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class FileInfo {

    private String path;

    private String filename;

    @JsonInclude(NON_NULL)
    private String url;

    private Long size;

    private FileType type;

    private String ext;

    @JsonInclude(NON_NULL)
    private Integer chunkNum;

    @JsonInclude(NON_NULL)
    private LocalDateTime modified;

    @JsonInclude(NON_NULL)
    private String md5;

    @JsonInclude(NON_NULL)
    private List<FileInfo> children;

    public FileInfo() {
    }

    public FileInfo(String path, String filename, Long size, FileType type, LocalDateTime modified, String md5) {
        this.path = path;
        this.filename = filename;
        this.size = size;
        this.type = type;
//        this.ext = ext;
        this.modified = modified;
        this.md5 = md5;
    }

    public FileInfo(String path, String filename, Long size, FileType type, String ext, LocalDateTime modified, String md5) {
        this.path = path;
        this.filename = filename;
        this.size = size;
        this.type = type;
        this.ext = ext;
        this.modified = modified;
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Integer getChunkNum() {
        return chunkNum;
    }

    public void setChunkNum(Integer chunkNum) {
        this.chunkNum = chunkNum;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<FileInfo> getChildren() {
        return children;
    }

    public void setChildren(List<FileInfo> children) {
        this.children = children;
    }

    public void append(FileInfo fileInfo) {
        if (null == this.children) {
            this.children = new ArrayList<>();
        }
        this.children.add(fileInfo);
    }
}

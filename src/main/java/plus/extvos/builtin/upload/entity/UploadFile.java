package plus.extvos.builtin.upload.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * @author Mingcai SHEN
 */
public class UploadFile implements Serializable {
    private String category;

    private String identifier;

    private String filename;

    private String root;

    private String prefix;

    private long size;

    private String originalName;

    private String checksum;

    public UploadFile() {

    }

    public UploadFile(String category, String identifier, String filename, String root, String prefix, long size, String origName, String checksum) {
        this.category = category;
        this.identifier = identifier;
        this.filename = filename;
        this.root = root;
        this.prefix = prefix;
        this.size = size;
        this.originalName = origName;
        this.checksum = checksum;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

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

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUri() {
        String s = StringUtils.trimLeadingCharacter(this.getFilename(),'/');
        if (null != this.getPrefix() && !this.getPrefix().isEmpty()) {
            s = String.join("/", StringUtils.trimTrailingCharacter(this.getPrefix(),'/'), this.getFilename());
        }
        return s;
    }
}

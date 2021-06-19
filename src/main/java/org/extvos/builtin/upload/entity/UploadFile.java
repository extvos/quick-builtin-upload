package org.extvos.builtin.upload.entity;

import java.io.Serializable;

/**
 * @author shenmc
 */
public class UploadFile implements Serializable {
    private String identifier;
    private String filename;
    private String root;
    private String prefix;
    private String url;
    private long size;
    private String originalName;
    private String checksum;

    public UploadFile() {

    }

    public UploadFile(String identifier, String filename, String root, String prefix, long size, String origName, String checksum) {
        this.identifier = identifier;
        this.filename = filename;
        this.root = root;
        this.prefix = prefix;
        this.url = prefix + "/" + filename;
        this.size = size;
        this.originalName = origName;
        this.checksum = checksum;
    }

    public void setFilename(String filename) {
        this.filename = filename;
        this.url = prefix + "/" + filename;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public String getUrl() {
        return url;
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
        this.url = prefix + "/" + this.filename;
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
}

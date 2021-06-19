package org.extvos.builtin.upload.controller;

import org.extvos.builtin.upload.service.StorageService;
import org.extvos.restlet.Assert;

/**
 * @author shenmc
 */
public class Uploader {
    private StorageService storageService;

    /**
     * Make a new uploader.
     *
     * @param storageService implemented storage service
     * @return new uploader
     */
    public static Uploader makeUploader(StorageService storageService) {
        Assert.notNull(storageService);
        Uploader ul = new Uploader();
        ul.storageService = storageService;
        return ul;
    }

    private Uploader() {

    }

    public Object doUpload() {
        return null;
    }
}

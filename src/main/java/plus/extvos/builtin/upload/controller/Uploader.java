package plus.extvos.builtin.upload.controller;

import plus.extvos.builtin.upload.service.StorageService;
import plus.extvos.restlet.Assert;

/**
 * @author Mingcai SHEN
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

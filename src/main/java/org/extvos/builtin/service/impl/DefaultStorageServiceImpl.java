package org.extvos.builtin.service.impl;

import org.extvos.builtin.service.StorageService;
import org.extvos.restlet.exception.RestletException;

import java.io.File;

/**
 * @author Mingcai SHEN
 */
public class DefaultStorageServiceImpl implements StorageService {
    @Override
    public boolean process(
        String root,
        String prefix,
        String filename,
        String category,
        String identifier,
        String originalFilename) throws RestletException {
        return false;
    }

    @Override
    public boolean exists(String filename, String identifier) {
        return new File(filename).exists();
    }

    @Override
    public boolean useTemporary() {
        return false;
    }
}

package org.extvos.builtin.service;

import org.extvos.restlet.exception.RestletException;

/**
 * @author shenmc
 */
public interface StorageService {
    /**
     * Process the file for given path
     *
     * @param filename         the relative path according to root.
     * @param prefix           the uri prefix for serving this file via http.
     * @param root             the root for saving this file.
     * @param category         the category of this file.
     * @param identifier       the identifier of file.
     * @param originalFilename the original filename of file.
     * @return true or false, file will be deleted when return true.
     * @throws RestletException if theres any exceptions.
     */
    boolean process(String root,
                    String prefix,
                    String filename,
                    String category,
                    String identifier,
                    String originalFilename) throws RestletException;

    /**
     * check if file exists, once if file exists, process will not be called
     *
     * @param filename   filename path
     * @param identifier file identity from client
     * @return true if file exists
     */
    boolean exists(String filename, String identifier);

    /**
     * Use temporary or not
     *
     * @return true if use temporary
     */
    boolean useTemporary();
}

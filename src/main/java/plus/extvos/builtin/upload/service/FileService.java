package plus.extvos.builtin.upload.service;

import plus.extvos.builtin.upload.dto.FileInfo;
import plus.extvos.common.exception.ResultException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileService {
    /**
     * Get file of directory information
     *
     * @param bucket
     * @param path
     * @param filename
     * @param withMd5
     * @return
     * @throws ResultException
     */
    FileInfo stat(String bucket, String path, String filename, boolean withMd5, boolean recursive) throws ResultException;

    /**
     * Create a directory
     *
     * @param bucket
     * @param path
     * @param filename
     * @return
     * @throws ResultException
     */
    FileInfo mkdir(String bucket, String path, String filename) throws ResultException;

    /**
     * Build file object
     *
     * @param bucket
     * @param path
     * @param filename
     * @return
     * @throws ResultException
     */
    File file(String bucket, String path, String filename) throws ResultException;

    /**
     * Read a file
     *
     * @param bucket
     * @param path
     * @param filename
     * @return
     * @throws ResultException
     */
    InputStream read(String bucket, String path, String filename) throws ResultException;

    /**
     * Save to a file
     *
     * @param bucket
     * @param path
     * @param filename
     * @return
     * @throws ResultException
     */
    OutputStream save(String bucket, String path, String filename) throws ResultException;

    /**
     * Delete a file of directory
     *
     * @param bucket
     * @param path
     * @param filename
     * @param recursive
     * @return
     * @throws ResultException
     */
    FileInfo delete(String bucket, String path, String filename, boolean recursive) throws ResultException;

    /**
     * Make a chunk temp file
     *
     * @param bucket
     * @param path
     * @param filename
     * @param chunkNum
     * @return
     * @throws ResultException
     */
    File chunk(String bucket, String path, String filename, int chunkNum) throws ResultException;
}

package plus.extvos.builtin.upload.service;

import plus.extvos.builtin.upload.entity.UploadFile;
import plus.extvos.builtin.upload.entity.UploadResult;
import plus.extvos.restlet.exception.RestletException;

import java.util.Map;

/**
 * @author Mingcai SHEN
 */
public interface StorageService {

    /**
     * Process the file for given path
     *
     * @param uploadFile uploadFile
     * @param category   the category of this file.
     * @param queries    the queries via http request
     * @return true or false, file will be deleted when return true.
     * @throws RestletException if theres any exceptions.
     */
    UploadResult process(UploadFile uploadFile,
                         String category,
                         Map<String, String> queries) throws RestletException;

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

    /**
     * Get storage root
     *
     * @return string of root
     */
    String root();

    /**
     * Get uri prefix
     *
     * @return string of prefix
     */
    String prefix();

    /**
     * Get temp path
     *
     * @return string of temp path
     */
    String temporary();

    /**
     * get chunkSize when upload in chunks
     *
     * @return bytes of chunk
     */
    default long chunkSize() {
        return 1024 * 1024 * 2L;
    }

    /**
     * get simultaneous task of upload
     *
     * @return num
     */
    default int simultaneous() {
        return 5;
    }

    /**
     * chunkNumberParameterName
     *
     * @return resumableChunkNumber
     */
    default String chunkNumberParameterName() {
        return "resumableChunkNumber";
    }

    /**
     * chunkSizeParameterName
     *
     * @return resumableChunkSize
     */
    default String chunkSizeParameterName() {
        return "resumableChunkSize";
    }

    /**
     * currentChunkSizeParameterName
     *
     * @return resumableCurrentChunkSize
     */
    default String currentChunkSizeParameterName() {
        return "resumableCurrentChunkSize";
    }

    /**
     * totalSizeParameterName
     *
     * @return resumableTotalSize
     */
    default String totalSizeParameterName() {
        return "resumableTotalSize";
    }

    /**
     * typeParameterName
     *
     * @return resumableType
     */
    default String typeParameterName() {
        return "resumableType";
    }

    /**
     * identifierParameterName
     *
     * @return resumableIdentifier
     */
    default String identifierParameterName() {
        return "resumableIdentifier";
    }

    /**
     * fileNameParameterName
     *
     * @return resumableFilename
     */
    default String fileNameParameterName() {
        return "resumableFilename";
    }

    /**
     * relativePathParameterName
     *
     * @return resumableRelativePath
     */
    default String relativePathParameterName() {
        return "resumableRelativePath";
    }

    /**
     * totalChunksParameterName
     *
     * @return resumableTotalChunks
     */
    default String totalChunksParameterName() {
        return "resumableTotalChunks";
    }

    /**
     * pathSegments
     *
     * @return 4
     */
    default int pathSegments() {
        return 4;
    }

}

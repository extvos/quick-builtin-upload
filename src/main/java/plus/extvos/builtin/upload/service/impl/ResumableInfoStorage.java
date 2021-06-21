package plus.extvos.builtin.upload.service.impl;

import plus.extvos.builtin.upload.entity.ResumableInfo;

import java.util.HashMap;

/**
 * @author Mingcai SHEN
 */
public class ResumableInfoStorage {
    //Single instance
    private ResumableInfoStorage() {
    }

    private static ResumableInfoStorage sInstance;

    public static synchronized ResumableInfoStorage getInstance() {
        if (sInstance == null) {
            sInstance = new ResumableInfoStorage();
        }
        return sInstance;
    }

    //resumableIdentifier --  ResumableInfo
    private HashMap<String, ResumableInfo> mMap = new HashMap<String, ResumableInfo>();

    /**
     * Get ResumableInfo from mMap or Create a new one.
     *
     * @param resumableChunkSize
     * @param resumableTotalSize
     * @param resumableIdentifier
     * @param resumableFilename
     * @param resumableRelativePath
     * @param resumableFilePath
     * @return
     */
    public synchronized ResumableInfo get(long resumableChunkSize, long resumableTotalSize,
                                          String resumableIdentifier, String resumableFilename,
                                          String resumableRelativePath, String resumableFilePath) {

        ResumableInfo info = mMap.get(resumableIdentifier);

        if (info == null) {
            info = new ResumableInfo();

            info.chunkSize = resumableChunkSize;
            info.totalSize = resumableTotalSize;
            info.identifier = resumableIdentifier;
            info.filename = resumableFilename;
            info.relativePath = resumableRelativePath;
            info.filePath = resumableFilePath;

            mMap.put(resumableIdentifier, info);
        }
        return info;
    }

    /**
     * É¾³ýResumableInfo
     *
     * @param info
     */
    public void remove(ResumableInfo info) {
        mMap.remove(info.identifier);
    }
}

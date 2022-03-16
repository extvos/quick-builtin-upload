package plus.extvos.builtin.upload.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.extvos.builtin.upload.dto.UploadResult;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResultStorage {
    //Single instance
    private ResultStorage() {
    }

    private static ResultStorage sInstance;
    private static final Logger log = LoggerFactory.getLogger(ResultStorage.class);

    public static synchronized ResultStorage getInstance() {
        if (sInstance == null) {
            sInstance = new ResultStorage();
        }
        return sInstance;
    }

    /**
     * resumableIdentifier --  ResumableInfo
     */
    private final Map<String, UploadResult> infoMap = new LinkedHashMap<String, UploadResult>();

    /**
     * Get ResumableInfo from mMap or Create a new one.
     *
     * @param identifier string
     * @return UploadResult
     */
    public synchronized UploadResult get(String identifier) {
//        log.debug("get:> {}, {}", identifier, chunk);
        UploadResult m = infoMap.get(identifier);
        return m;
    }

    public synchronized void set(String identifier, UploadResult result) {
        infoMap.put(identifier, result);
    }

    /**
     * É¾³ýResumableInfo
     *
     * @param identifier string
     */
    public synchronized void remove(String identifier) {
        infoMap.remove(identifier);
    }
}

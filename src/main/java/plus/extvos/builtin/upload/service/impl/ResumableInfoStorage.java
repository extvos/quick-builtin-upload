package plus.extvos.builtin.upload.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.extvos.builtin.upload.dto.ResumableInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mingcai SHEN
 */
public class ResumableInfoStorage {
    //Single instance
    private ResumableInfoStorage() {
    }

    private static ResumableInfoStorage sInstance;
    private static final Logger log = LoggerFactory.getLogger(ResumableInfoStorage.class);

    public static synchronized ResumableInfoStorage getInstance() {
        if (sInstance == null) {
            sInstance = new ResumableInfoStorage();
        }
        return sInstance;
    }

    /**
     * resumableIdentifier --  ResumableInfo
     */
    private final Map<String, Map<Integer, ResumableInfo>> infoMap = new LinkedHashMap<String, Map<Integer, ResumableInfo>>();

    /**
     * Get ResumableInfo from mMap or Create a new one.
     *
     * @param identifier string
     * @param chunk integer
     * @return ResumableInfo
     */
    public synchronized ResumableInfo get(String identifier, Integer chunk) {
//        log.debug("get:> {}, {}", identifier, chunk);
        Map<Integer, ResumableInfo> m = infoMap.get(identifier);
        if (null == m) {
            return null;
        } else {
            return m.get(chunk);
        }
    }

    /**
     * get size
     * @param identifier string
     * @return size as integer
     */
    public synchronized int size(String identifier) {

        Map<Integer, ResumableInfo> m = infoMap.get(identifier);
        if (null == m) {
//            log.debug("size:> {} = {}", identifier, 0);
            return 0;
        } else {
//            log.debug("size:> {} = {}", identifier, m.size());
            return m.size();
        }
    }

    /**
     * set resumable info
     * @param info in ResumableInfo
     */
    public synchronized void set(ResumableInfo info) {
//        log.debug("set:> {}, {}, {}", info.identifier, info.chunkNum, info.chunkFilename);
        Map<Integer, ResumableInfo> m = infoMap.get(info.identifier);
        if (null == m) {
            m = new LinkedHashMap<Integer, ResumableInfo>();
            m.put(info.chunkNum, info);
            infoMap.put(info.identifier, m);
        } else {
            m.put(info.chunkNum, info);
            infoMap.put(info.identifier, m);
        }
//        log.debug("set:> {}", m);
//        log.debug("set:> {}", infoMap);
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

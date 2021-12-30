package plus.extvos.builtin.upload.service.impl;

import plus.extvos.builtin.upload.entity.ResumableInfo;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * resumableIdentifier --  ResumableInfo
     */
    private final HashMap<String, Map<Integer, ResumableInfo>> infoMap = new HashMap<String, Map<Integer, ResumableInfo>>();

    /**
     * Get ResumableInfo from mMap or Create a new one.
     *
     * @param identifier
     * @param chunk
     * @return
     */
    public synchronized ResumableInfo get(String identifier, Integer chunk) {

        Map<Integer, ResumableInfo> m = infoMap.get(identifier);
        if (null == m) {
            return null;
        } else {
            return m.get(chunk);
        }
    }

    public synchronized void set(ResumableInfo info) {

        Map<Integer, ResumableInfo> m = infoMap.get(info);
        if (null == m) {
            m = new HashMap<Integer, ResumableInfo>();
            m.put(info.chunkNum, info);
            infoMap.put(info.identifier, m);
        } else {
            m.put(info.chunkNum, info);
        }
    }

    /**
     * É¾³ýResumableInfo
     *
     * @param identifier
     */
    public void remove(String identifier) {
        infoMap.remove(identifier);
    }
}

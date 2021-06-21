package plus.extvos.builtin.upload.controller;

import plus.extvos.restlet.Code;

/**
 * @author Mingcai SHEN
 */

public enum UploadResultCode implements Code {
    /**
     *
     */
    FORBIDDEN_CREATE(40311, "Not Allowed To Create"),
    FILE_NOT_EXISTS(40411, "File Not Exists"),
    SEGMENT_NOT_EXISTS(40412, "Segment Not Exists"),
    /**
     *
     */
    UPLOAD_CONFIG_ERROR(50011, "Upload Config Error"),
    ;

    private final int value;
    private final String desc;

    UploadResultCode(int v, String d) {
        value = v;
        desc = d;
    }


    @Override
    public int value() {
        return this.value;
    }

    @Override
    public int status() {
        return this.value / 100;
    }

    @Override
    public String desc() {
        return this.desc;
    }
}

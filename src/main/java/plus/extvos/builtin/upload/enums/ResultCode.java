package plus.extvos.builtin.upload.enums;

import plus.extvos.common.Code;

/**
 * @author Mingcai SHEN
 */

public enum ResultCode implements Code {
    /**
     *
     */
    FORBIDDEN_READ(40311, "Not Allowed To Read"),
    FORBIDDEN_CREATE(40312, "Not Allowed To Create"),
    FORBIDDEN_DELETE(40313, "Not Allowed To Delete"),
    FILE_NOT_EXISTS(40411, "File Not Exists"),
    SEGMENT_NOT_EXISTS(40412, "Segment Not Exists"),
    /**
     *
     */
    UPLOAD_CONFIG_ERROR(50011, "Upload Config Error"),
    ;

    private final int value;
    private final String desc;

    ResultCode(int v, String d) {
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

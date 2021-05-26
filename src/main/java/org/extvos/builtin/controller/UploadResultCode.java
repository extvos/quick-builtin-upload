package org.extvos.builtin.controller;

import org.extvos.restlet.Code;

/**
 * @author shenmc
 */

public enum UploadResultCode implements Code {
    /**
     *
     */
    FORBIDDEN_CREATE(40311, "Not Allowed To Create"),
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

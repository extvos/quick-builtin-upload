package org.extvos.builtin.upload.entity;

import java.io.Serializable;

/**
 * @author shenmc
 */
public class UploadResult implements Serializable {
    private Object result;
    private boolean processed;

    public UploadResult() {

    }

    public UploadResult(Object ret, boolean processed) {
        this.result = ret;
        this.processed = processed;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}

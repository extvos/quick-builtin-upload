package plus.extvos.builtin.upload.entity;

import java.io.Serializable;

/**
 * @author Mingcai SHEN
 */
public class UploadResult implements Serializable {
    private UploadFile result;
    private boolean processed;

    public UploadResult() {

    }

    public UploadResult(UploadFile ret, boolean processed) {
        this.result = ret;
        this.processed = processed;
    }

    public UploadFile getResult() {
        return result;
    }

    public void setResult(UploadFile result) {
        this.result = result;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}

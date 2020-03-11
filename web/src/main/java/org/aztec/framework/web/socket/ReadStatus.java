package org.aztec.framework.web.socket;

public enum ReadStatus {

    READ(1),UNREAD(0);
    
    int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private ReadStatus(int code) {
        this.code = code;
    }
    
    
}

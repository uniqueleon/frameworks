package org.aztec.framework.web.socket;

public enum MessageStatus {

    UNSENT(0),SEND(1);
    
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private MessageStatus(int code) {
        this.code = code;
    }
    
    
}

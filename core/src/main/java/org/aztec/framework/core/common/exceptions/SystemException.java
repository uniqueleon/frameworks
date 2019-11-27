package org.aztec.framework.core.common.exceptions;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The super class for all System exceptions
 * 
 * @author KingsHunter
 * @createDate May 30th,2016
 */
public abstract class SystemException extends FastRuntimeException {

    private static final long serialVersionUID = 7582442069463082499L;
    static AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());
    String id;

    protected String status;

    protected String msg;

    public SystemException() {
        super();
        setId();
    }

    public SystemException(String message) {
        super(message);
        setId();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        setId();
    }

    void setId() {
        long nid = atomicLong.incrementAndGet();
        id = Long.toString(nid, 26);
    }

    public abstract String getErrorTitle();

    public abstract String getErrorDescription();

    public boolean isSourceAvailable() {
        return this instanceof SourceAttachment;
    }

    public Integer getLineNumber() {
        return -1;
    }

    public String getSourceFile() {
        return "";
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}

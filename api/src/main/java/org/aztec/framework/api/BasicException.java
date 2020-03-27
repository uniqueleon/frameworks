package org.aztec.framework.api;

public class BasicException extends Exception {

    private String errorCode;
    
    public static interface BasicErrorCodes{
        String BASIC_ERROR_PREFIX = "FW_BASIC_E_";
        String FIELD_NOT_FOUND = BASIC_ERROR_PREFIX + "001";
        String ILLEAGAL_ARGUMENTS = BASIC_ERROR_PREFIX +  "002";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public BasicException() {
        super();
    }


    public BasicException(String errorCode,String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BasicException(String errorCode,Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
    
    public BasicException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}

package org.aztec.framework.mybatis.exceptions;

/**
 * 
 * @author KingsHunter
 * @createDate May 30th,2016
 *
 */
public class BusinessException extends SystemException {

    private static final long serialVersionUID = -5542010480764511610L;

    private String status;

    private String msg;

    public BusinessException() {
        super("系统内部错误");
    }

    public BusinessException(String status, String msg) {
        super(msg);
        this.status = status;
        this.msg = msg;
    }

    public BusinessException(String status, String message, Object... args) {
        super(String.format(message, args));
        this.status = status;
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

    public String getErrorTitle() {
        return null;
    }

    public String getErrorDescription() {
        return null;
    }

}

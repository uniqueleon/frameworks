package org.aztec.framework.api.rest.entity;

import org.aztec.framework.api.rest.constant.StatusCodes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RestResult<T> {

    private boolean success;
    private String status;
    private String msg;
    private T data;

    public RestResult() {
    }

    public RestResult(boolean success, String status, String msg) {
        this.success = success;
        this.status = status;
        this.msg = msg;
    }
    
    public RestResult(boolean success, String status, String msg,T data) {
        this.success = success;
        this.status = status;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> RestResult<T> successResult(T data) {
        RestResult<T> rr = new RestResult<>();
        rr.success = true;
        rr.status = StatusCodes.COMMON_SUCCESS;
        rr.data = data;
        return rr;
    }

}

package org.aztec.framework.web.controller;

import org.aztec.framework.api.rest.constant.StatusCodes;
import org.aztec.framework.api.rest.entity.RestResult;
import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.web.protocol.ProtocolUtils;

public abstract class BaseController {

    private static final String DEFAULT_NOT_NULL_MSG = "参数不能为空！";
    private static final String OBJ_NOT_NULL_MSG = "对象不能为空！";
    private static final String ID_NOT_NULL_MSG = "id不能为空！";

    public static void validateNotNull(Object obj, String msg){
        if (obj == null)
            throw new BusinessException(StatusCodes.PARAM_KEY_ILLEGAL_ERROR, msg == null ? DEFAULT_NOT_NULL_MSG : msg);
    }

    protected <T> String renderSuccessJson(T data) {
        return ProtocolUtils.renderSuccessJson(data);
    }

    protected <T> String renderSuccessJson(String status, String msg, T data) {
        return ProtocolUtils.renderSuccessJson(status, msg, data);
    }

    protected <T> String renderFailJson(String status, String msg) {
        return ProtocolUtils.renderFailJson(status, msg);
    }

    protected <T> String renderJson(boolean success, String status, String msg, T data) {
        return ProtocolUtils.renderJson(success, status, msg, data);
    }

    protected <T> RestResult<T> populateFailResponseTemplate(String status, String msg) {
        return ProtocolUtils.populateFailResponseTemplate(status, msg);
    }

    protected <T> RestResult<T> populateSuccessResponseTemplate(T data) {
        return ProtocolUtils.populateSuccessResponseTemplate(data);
    }

    protected <T> RestResult<T> populateSuccessResponseTemplate(String status, String msg, T data) {
        return ProtocolUtils.populateSuccessResponseTemplate(status, msg, data);
    }

    
}

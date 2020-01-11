package org.aztec.framework.web.protocol;

import org.aztec.framework.api.rest.constant.StatusCodes;
import org.aztec.framework.api.rest.entity.RestResult;
import org.aztec.framework.core.utils.JsonUtils;

public final class ProtocolUtils {

    public static <T> String renderSuccessJson(T data) {
        RestResult<T> baseResponse = populateSuccessResponseTemplate(data);
        return JsonUtils.obj2String(baseResponse);
    }

    public static <T> String renderSuccessJson(String status, String msg, T data) {
        RestResult<T> baseResponse = populateSuccessResponseTemplate(status, msg, data);
        return JsonUtils.obj2String(baseResponse);
    }

    public static <T> String renderFailJson(String status, String msg) {
        RestResult<T> baseResponse = populateFailResponseTemplate(status, msg);
        return JsonUtils.obj2String(baseResponse);
    }

    public static <T> String renderJson(boolean success, String status, String msg, T data) {
        RestResult<T> baseResponse = populateResponseTemplate(success, status, msg, data);
        return JsonUtils.obj2String(baseResponse);
    }

    public static <T> RestResult<T> populateFailResponseTemplate(String status, String msg) {
        RestResult<T> baseResponse = populateResponseTemplate(false, status, msg);
        return baseResponse;
    }

    public static <T> RestResult<T> populateSuccessResponseTemplate(T data) {
        RestResult<T> baseResponse = populateSuccessResponseTemplate(StatusCodes.COMMON_SUCCESS, "Ok", data);
        return baseResponse;
    }

    public static <T> RestResult<T> populateSuccessResponseTemplate(String status, String msg, T data) {
        RestResult<T> baseResponse = populateResponseTemplate(true, status, msg, data);
        return baseResponse;
    }

    private static <T> RestResult<T> populateResponseTemplate(boolean success, String status, String msg) {
        RestResult<T> baseResponse = new RestResult<T>(success, status, msg);
        return baseResponse;
    }

    private static <T> RestResult<T> populateResponseTemplate(boolean success, String status, String msg, T data) {
        RestResult<T> baseResponse = new RestResult<T>(success, status, msg, data);
        return baseResponse;
    }

}

package org.aztec.framework.web.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.api.rest.constant.StatusCodes;
import org.aztec.framework.api.rest.entity.RestResult;
import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.core.common.exceptions.UnexpectedException;
import org.aztec.framework.web.protocol.ProtocolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 
 * @author KingsHunter
 * @createDate May 28th,2016
 * 
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public <T> RestResult<T> businessErrorHandler(HttpServletRequest req, BusinessException e) throws Exception {
        logger.info("业务异常：" + e.getMsg());
        return ProtocolUtils.populateFailResponseTemplate(e.getStatus(), e.getMsg());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public <T> RestResult<T> paramsErrorHandler(HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
        String msgFormat = "请求参数错误, argument name: %s, method name: %s";
        String msg = String.format(msgFormat, ex.getName(), ex.getParameter().getMethod().getName());
        logger.error(msg);
        return ProtocolUtils.populateFailResponseTemplate(StatusCodes.PARAM_VALUE_ILLEGAL_ERROR, msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public <T> RestResult<T> internalErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("内部异常: ", e);
        return ProtocolUtils.populateFailResponseTemplate(StatusCodes.INTERNAL_ERROR, "internal error");
    }

    @ExceptionHandler(UnexpectedException.class)
    @ResponseBody
    public <T> RestResult<T> unkownErrorHandler(HttpServletRequest req, UnexpectedException e) throws Exception {
        logger.error("未知异常: " + e.getErrorDescription());
        String errorMessage = e.getMessage();
        if (StringUtils.isBlank(errorMessage))
            errorMessage = "未知异常";
        return ProtocolUtils.populateFailResponseTemplate(StatusCodes.UNKOWN_ERROR, errorMessage);
    }

}

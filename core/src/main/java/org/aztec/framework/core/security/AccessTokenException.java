package org.aztec.framework.core.security;

import org.aztec.framework.core.common.exceptions.BusinessException;

/**
 * Access Token 相关异常
 * @author 01390615
 *
 */
public class AccessTokenException extends BusinessException {

    public AccessTokenException(String status, String message, Object... args) {
        super(status, message, args);
        // TODO Auto-generated constructor stub
    }

    public AccessTokenException(String status, String msg) {
        super(status, msg);
        // TODO Auto-generated constructor stub
    }

    /**
     * Access Token 相关的错误码
     * @author liming
     *
     */
    public static interface ErrorCode{
        String TOKEN_MISSING = "ACCESS_TOKEN_MISSING";
        String TOKEN_GENERATE_FAIL = "ACCESS_TOKEN_GENERATE_FAIL";
        
    }
}

package org.aztec.framework.api.rest;

public interface BasicClientConstant {

    public static final String APP_NAME = "BASIC";

    public static final String RESPONSE_SUCCESS_MSG = "OK";

    public interface AuthErrorCodes {
        public static final String LOGIN_FAIL = "AUTH_ERR_001";
        public static final String ACCOUNT_NOT_ACTIVE = "AUTH_ERR_002";

    }
}

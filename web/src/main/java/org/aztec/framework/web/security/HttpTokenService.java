package org.aztec.framework.web.security;

import javax.servlet.http.HttpServletRequest;

public interface HttpTokenService extends TokenService {

    public static final String COOKIES_TOKEN_NAME = "BMS_ACCESS_TOKEN";
    /**
     * 从HTTP请求中获取TOKEN
     * @param request
     * @return
     */
    public Token getTokenFromRequest(HttpServletRequest request);
}

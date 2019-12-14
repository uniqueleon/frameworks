package org.aztec.framework.web.security.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.web.security.HttpTokenService;
import org.aztec.framework.web.security.Token;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class RedisHttpTokenService extends BaseTokenService implements HttpTokenService {

    
    


    @Override
    public Token getTokenFromRequest(HttpServletRequest request) {
        String tokenID = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isEmpty(tokenID)){
            return null;
        }
        //RedisManage redisManager = RedisManage.getInstance();

        //String realToken = redisManager.get(tokenID);
        //Token retToken = parse(realToken);
        //retToken.setID(tokenID);
        //return retToken;
        //String cookieToken = tokenID;
        //String cookieToken = CookieUtils.getCookie(request, COOKIES_TOKEN_NAME);
        
        //return parse(cookieToken);
        return null;
    }

    @Override
    public void persist(Token token) throws BusinessException {
        //RedisManage.getInstance().set(token.getID(), token.getAsString(), new Long(token.getTtl() / 1000).intValue());
    }

}

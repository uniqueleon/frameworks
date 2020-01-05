package org.aztec.framework.web.security.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.web.security.HttpTokenService;
import org.aztec.framework.web.security.Token;
import org.aztec.framework.web.security.TokenMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.sjsc.framework.redis.core.RedisManage;

@Component
public class RedisHttpTokenService extends BaseTokenService implements HttpTokenService {

    
    private static final Logger LOG = LoggerFactory.getLogger(RedisHttpTokenService.class);

    @Override
    public Token getTokenFromRequest(HttpServletRequest request) {
        String tokenID = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isEmpty(tokenID)){
            LOG.error("TOKEN ID is EMPTY!");
            throw new TokenMissingException("TOKEN ID is EMPTY!");
        }
        RedisManage redisManager = RedisManage.getInstance();
        String realToken = redisManager.get(tokenID);
        if(realToken != null){
            Token retToken = parse(realToken);
            retToken.setID(tokenID);
            return retToken;
        }
        else {
            LOG.error("TOKEN[" + tokenID + "] is missing!");
            throw new TokenMissingException("TOKEN[" + tokenID + "] is missing!");
        }
        //String cookieToken = tokenID;
        //String cookieToken = CookieUtils.getCookie(request, COOKIES_TOKEN_NAME);
        
        //return parse(cookieToken);
    }

    @Override
    public void persist(Token token) throws BusinessException {
        RedisManage.getInstance().set(token.getID(), token.getAsString(), new Long(token.getTtl() / 1000).intValue());
    }

    @Override
    public void remove(Token token) throws BusinessException {
        if(token != null){
            RedisManage.getInstance().del(token.getID());
        }
    }

}

package org.aztec.framework.web.security.impl;

import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.disconf.items.SessionEncryptConfig;
import org.aztec.framework.web.security.Authentication;
import org.aztec.framework.web.security.Credentials;
import org.aztec.framework.web.security.CredentialsBuilder;
import org.aztec.framework.web.security.Token;
import org.aztec.framework.web.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("tokenService")
public abstract class BaseTokenService implements TokenService{
    
    @Autowired
    SessionEncryptConfig encrypConfig;
    @Autowired
    CredentialsBuilder credentialsBUilder;
    
    private static final String TOKEN_CONTENT_SEPERATOR = "###";

    @Override
    public Token generateToken(Authentication auth) {
        // TODO Auto-generated method stub
        if(auth.isAuthencated()){
            Long nowDate = System.currentTimeMillis();
            String tokenStr = auth.getCredentials().getAsString();
            long ttl = encrypConfig.ttlToMilliSec();
            tokenStr += TOKEN_CONTENT_SEPERATOR + nowDate;
            tokenStr += TOKEN_CONTENT_SEPERATOR + ttl;
            //String encryptContent = new String(CodecUtils.encryptAES(tokenStr, encrypConfig.getNewKey()));
            
            return new SimpleToken(tokenStr,nowDate, ttl,true);
        }
        return null;
    }
    
    

    @Override
    public Token renew(Token token) throws BusinessException {

        Long nowDate = System.currentTimeMillis();
        String tokenStr = token.getAsString().split(TOKEN_CONTENT_SEPERATOR)[0];
        long ttl = encrypConfig.ttlToMilliSec();
        tokenStr += TOKEN_CONTENT_SEPERATOR + nowDate;
        tokenStr += TOKEN_CONTENT_SEPERATOR + ttl;
        Token newToken = new SimpleToken(tokenStr,nowDate, ttl,true);
        persist(newToken);
        return newToken;
    }



    @Override
    public boolean isValid(Token token) {
        if(token != null){
            Long nowDate = System.currentTimeMillis();
            if(!token.isExpired() && (nowDate - token.getTimestamp() < token.getTtl())){
                return true;
            }
        }
        return false;
    }

    @Override
    public Credentials getCredentials(Token token) throws BusinessException {
        if(isValid(token)){
            return credentialsBUilder.build(token.getAsString());
        }
        return null;
    }

    @Override
    public Token parse(String tokenStr) throws BusinessException {
        if(tokenStr == null || tokenStr.isEmpty()){
            return null;
        }
        String[] dataArray = tokenStr.split(TOKEN_CONTENT_SEPERATOR);
        SimpleToken token =  new SimpleToken(dataArray[0], Long.parseLong(dataArray[1]), Long.parseLong(dataArray[2]), true);
        token.setExpired(isValid(token));
        return token;
    }

}

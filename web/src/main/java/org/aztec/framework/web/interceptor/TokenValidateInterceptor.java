package org.aztec.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aztec.framework.disconf.items.SessionEncryptConfig;
import org.aztec.framework.web.security.HttpTokenService;
import org.aztec.framework.web.security.Token;
import org.aztec.framework.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.common.net.HttpHeaders;

@Component
public class TokenValidateInterceptor implements HandlerInterceptor{
    
    @Autowired
    HttpTokenService tokenService;
    
    @Autowired
    SessionEncryptConfig encryptConfig;
    @Value("${spring.application.name}")
    String appName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        if(isLoginRequest(request)){
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        
        if(!isInnerRequest(request)){
            Token token = tokenService.getTokenFromRequest(request);
            if(token == null || !tokenService.isValid(token)){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            Long now = System.currentTimeMillis();
            Long beginTime = token.getTimestamp();
            Long intervalTime = encryptConfig.renewIntervalToMS();
            if(now - beginTime > intervalTime){
                refreshToken(response,token);
            }
        }
        /*if(!isInnerRequest(request) && !canSkipRequest(request) && !isTokenExist(request)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }*/
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
    
    public boolean isValidateEnabled(){
        return Boolean.parseBoolean(encryptConfig.getTokenValidEnabled());
    }
    
    public boolean isTokenExist(HttpServletRequest request){
        return tokenService.getTokenFromRequest(request) != null;
    }
    
    private void refreshToken(HttpServletResponse response,Token token){
        Token newToken = tokenService.renew(token);
        response.setHeader(HttpHeaders.AUTHORIZATION, newToken.getID());
    }


    private boolean isLoginRequest(HttpServletRequest request){
        
        return WebUtils.isLoginRequest(request.getRequestURI()) || canSkipRequest(request);
    }
    
    
    private boolean canSkipRequest(HttpServletRequest request){
        String reqUri = request.getRequestURI();
        String[] skipSuffix = encryptConfig.getNocheckUrls();
        for(String suffix : skipSuffix){
            if(reqUri.endsWith(suffix)){
                return true;
            }
        }
        return false;
    }
    
    private boolean isInnerRequest(HttpServletRequest request){
        String reqUri = request.getRequestURI();
        if(!reqUri.startsWith("/" + appName.toUpperCase())){
            return true;
        }
        return false;
    }
}

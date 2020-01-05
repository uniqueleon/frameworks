package org.aztec.framework.web.util;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    
    public static void writeCookies(HttpServletResponse response,Map<String,String> cookies){
        for(String key : cookies.keySet()){
            response.addCookie(new Cookie(key,cookies.get(key)));
        }
    }
    
    public static String getCookie(HttpServletRequest request,String name){
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }
        return null;
    }
}

package org.aztec.framework.web.util;

public class WebUtils {

    private static final String LOGIN_SUFFIX_PATH = "/auth/login";
    

    public static boolean isLoginRequest(String path){
        if(path.endsWith(LOGIN_SUFFIX_PATH)){
            return true;
        }
        return false;
    }
    
    
}

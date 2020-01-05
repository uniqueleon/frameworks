package org.aztec.framework.web.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.aztec.framework.core.utils.ClassScanUtils;
import org.aztec.framework.core.utils.ClassScanUtils.ClassSelectStrategy;
import org.aztec.framework.web.security.impl.UserDataCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class UserDataFetcherInitializer {
    
    public static final String FETCHER_KEY_WORD = "Fetcher";
    
    public static final Logger LOG = LoggerFactory.getLogger(UserDataFetcherInitializer.class);

    public static void init(String path) throws ClassNotFoundException, URISyntaxException, IOException{
        //UserDataCredentials.registUserDetailService(USER_DATA_KEYS.ROLE_IDS, "roleFetcher");
        List<Class> clazz = 
                ClassScanUtils.scan(path, Component.class, false, ClassSelectStrategy.ANNOTAION);
        LOG.info((clazz.size() > 0 ? clazz.size() + "classes found " : "NO CLASSES FOUND!: ") + "IN PATH[" + path + "]!");
        for(Class clz : clazz){
            Component component =  (Component) clz.getAnnotation(Component.class);
            String beanName = component.value();
            String objName = beanName.replace(FETCHER_KEY_WORD, "");
            UserDataCredentials.registUserDetailService(objName, beanName);
        }
        
    }
}

package org.aztec.framework.web.security.impl;

import org.aztec.framework.web.security.Principals;

public class StringifyPrincipals implements Principals{
    
    private String content;


    @Override
    public <T> T cast(Class<? extends T> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAsString() {
        return content;
    }
    
    

}

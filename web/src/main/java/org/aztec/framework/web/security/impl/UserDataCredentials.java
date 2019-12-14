package org.aztec.framework.web.security.impl;

import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.web.security.Credentials;

public class UserDataCredentials implements Credentials{
    
    private String content;
    private String account;
    private Long id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public <T> T cast(Class<? extends T> clazz) {
        if(clazz.isAssignableFrom(this.getClass())){
            return (T) this;
        }
        return null;
    }
    
    private static final String CONTENT_SEPERATOR = "$$$";

    @Override
    public void parse(String content) throws BusinessException {
        // TODO Auto-generated method stub
        String[] contentArr = content.split(CONTENT_SEPERATOR);
        this.account = contentArr[0];
        this.id = Long.parseLong(contentArr[1]);
    }

    public UserDataCredentials(String account, Long id) {
        super();
        this.account = account;
        this.id = id;
        this.content = id + CONTENT_SEPERATOR + account;
    }

    public UserDataCredentials() {
        super();
    }

    @Override
    public String getAsString() {
        return content;
    }
    
    

}

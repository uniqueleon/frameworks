package org.aztec.framework.web.security.impl;

import org.aztec.framework.web.security.Authentication;
import org.aztec.framework.web.security.Credentials;
import org.aztec.framework.web.security.Principals;

public class UsernamePasswordAuthentication implements Authentication {
    
    private String username;
    private String password;
    private Long userId;
    private boolean authenticated;
    private Credentials credentials;

    public UsernamePasswordAuthentication(String username, String password, Long userId,
            boolean authenticated) {
        super();
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.authenticated = authenticated;
        this.credentials = new UserDataCredentials(username, userId);
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public Principals getPrincipals() {
        return new StringifyPrincipals();
    }

    @Override
    public boolean isAuthencated() {
        // TODO Auto-generated method stub
        return authenticated;
    }

    
}

package org.aztec.framework.web.security.impl;

import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.web.security.Credentials;
import org.aztec.framework.web.security.CredentialsBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserDataCredentialBuilder implements CredentialsBuilder {

    @Override
    public Credentials build(String content) throws BusinessException {
        UserDataCredentials credentials = new UserDataCredentials();
        credentials.parse(content);
        return credentials;
    }

}

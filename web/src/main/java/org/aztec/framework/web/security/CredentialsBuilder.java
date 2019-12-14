package org.aztec.framework.web.security;

import org.aztec.framework.core.common.exceptions.BusinessException;

public interface CredentialsBuilder {

    public Credentials build(String content) throws BusinessException;
}

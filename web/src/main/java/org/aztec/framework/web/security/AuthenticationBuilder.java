package org.aztec.framework.web.security;

import org.aztec.framework.core.common.exceptions.BusinessException;

public interface AuthenticationBuilder {

    public Authentication build(Object... args) throws BusinessException;
}

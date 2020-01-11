package org.aztec.framework.web.security;

import org.aztec.framework.api.rest.constant.StatusCodes;
import org.aztec.framework.core.common.exceptions.BusinessException;

public class TokenMissingException extends BusinessException {

    public TokenMissingException(String msg){
        super(StatusCodes.TOKEN_MISSING, msg);
    }
}

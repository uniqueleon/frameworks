package org.aztec.framework.web.security;

import com.sjsc.framework.api.restful.constant.StatusCodes;
import com.sjsc.framework.core.exceptions.BusinessException;

public class TokenMissingException extends BusinessException {

    public TokenMissingException(String msg){
        super(StatusCodes.TOKEN_MISSING, msg);
    }
}

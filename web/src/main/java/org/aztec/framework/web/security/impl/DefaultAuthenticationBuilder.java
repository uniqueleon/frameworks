package org.aztec.framework.web.security.impl;

import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.web.security.Authentication;
import org.aztec.framework.web.security.AuthenticationBuilder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationBuilder implements AuthenticationBuilder {

    @Override
    public Authentication build(Object... args) throws BusinessException{
        // TODO Auto-generated method stub
        if(args.length != 3){
            return new UsernamePasswordAuthentication((String)args[0], (String)args[1], (Long)args[2], (boolean)args[3]);
        }
        return null;
    }

}

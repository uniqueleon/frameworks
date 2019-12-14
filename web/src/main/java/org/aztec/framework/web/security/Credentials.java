package org.aztec.framework.web.security;

import org.aztec.framework.core.ObjectAdapter;
import org.aztec.framework.core.StringifyObject;
import org.aztec.framework.core.common.exceptions.BusinessException;

/**
 * 登陆凭证，当用户正常登证后就会获得这个对象。是用户的身份信息
 * @author 01390615
 *
 */
public interface Credentials extends ObjectAdapter,StringifyObject{

    public void parse(String content) throws BusinessException;
    
}

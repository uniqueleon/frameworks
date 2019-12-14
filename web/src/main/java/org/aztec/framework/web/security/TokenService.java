package org.aztec.framework.web.security;

import org.aztec.framework.core.common.exceptions.BusinessException;

/**
 * TOKEN服务
 * @author 01390615
 *
 */
public interface TokenService {

    /**
     * 从登陆认证中生成TOKEN
     * @param auth
     * @return
     * @throws BusinessException
     */
    public Token generateToken(Authentication auth) throws BusinessException;
    
    
    public void persist(Token token) throws BusinessException;
    /**
     * 是否有效TOKEN
     * @param token
     * @return
     * @throws BusinessException
     */
    public boolean isValid(Token token) throws BusinessException;
    /**
     * 从TOKEN中抽取用户身份信息 
     * @return
     */
    public Credentials getCredentials(Token token) throws BusinessException;
    
    /**
     * 从字符串中解释TOKEN
     * @param tokenStr
     * @return
     * @throws BusinessException
     */
    public Token parse(String tokenStr)throws BusinessException ;
    
    /**
     * 重新刷新TOKEN
     * @param token
     * @return
     * @throws BusinessException
     */
    public Token renew(Token token) throws BusinessException;
}

package org.aztec.framework.core.security;

/**
 * 第三方应用Access Token 管理器
 * @author 01390615
 *
 */
public interface AccessTokenManager {

    /**
     * 生成Access Token
     * @param appKey
     * @param ttl
     * @param renewInterval
     * @return
     * @throws AccessTokenException
     */
    public AccessToken generate(String appKey,Long ttl,Long renewInterval)throws AccessTokenException;
    /**
     * 获取Access Token
     * @param appKey
     * @return
     * @throws AccessTokenException
     */
    public AccessToken getToken(String appKey) throws AccessTokenException;
    /**
     * 使某个appkey token过期
     * @param appKey
     * @throws AccessTokenException
     */
    public void expire(String appKey) throws AccessTokenException;
}

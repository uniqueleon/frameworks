package org.aztec.framework.core.security;

/**
 * 随机字串管理器，主要用于联合时间戳防范重放攻击.
 * @author 01390615
 *
 */
public interface NonceManager {

    public boolean isNonceExists(String key,String nonce);
    
    public void setNonce(String key,String nonce,Long ttl);
}

package org.aztec.framework.core.security;

/**
 * 第三方应用访问TOKEN
 * @author 01390615
 *
 */
public interface AccessToken {

    /**
     * 获取存续时间
     * @return
     */
    public Long ttl();
    /**
     * 以字符串形式获取Access Token
     * @return
     */
    public String getAsString();
    /**
     * 是否过期
     * @return
     */
    public boolean isExpired();
    /**
     * 获取创建时间
     * @return
     */
    public Long getCreatedTime();
    /**
     * 设置是否过期
     * @param expired
     */
    public void setExpired(boolean expired);
    
    String DEFAULT_DATA_SEPERATOR = "_";
}

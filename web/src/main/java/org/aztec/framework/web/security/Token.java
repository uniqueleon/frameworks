package org.aztec.framework.web.security;

import org.aztec.framework.core.StringifyObject;

/**
 * 系统生成的登陆令牌
 * @author 01390615
 *
 */
public interface Token extends StringifyObject{

    /**
     * 生成字符串形式TOKEN
     * @return
     */
    public String toString();
    
    /**
     * TOKEN的唯一标识
     * @return
     */
    public String getID();
    
    
    public void setID(String id);
    /**
     * 获取当前可存活时间
     * @return
     */
    public long getTtl();
    /**
     * 判 断当前TOKEN是否已过啊
     * @return
     */
    public boolean isExpired();
    /**
     * 获取生成时间截
     * @return
     */
    public long getTimestamp();
    
    public void setExpired(boolean expired);
}

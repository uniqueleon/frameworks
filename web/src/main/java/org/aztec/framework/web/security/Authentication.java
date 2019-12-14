package org.aztec.framework.web.security;

/**
 * 登陆鉴权对象
 * @author 01390615
 *
 */
public interface Authentication {

    /**
     * 登陆凭证
     * @return
     */
    public Credentials getCredentials();
    public Principals getPrincipals();
    /**
     * 是否已授权
     * @return
     */
    public boolean isAuthencated();
}

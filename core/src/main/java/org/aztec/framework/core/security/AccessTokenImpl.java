package org.aztec.framework.core.security;

/**
 * Access Token 默认实现
 * @author 01390615
 *
 */
public class AccessTokenImpl implements AccessToken{
    
    private Long ttl = 24 * 3600000l;
    private String token;
    private boolean expired = false;
    private Long createdTime;

    @Override
    public Long ttl() {
        // TODO Auto-generated method stub
        return ttl;
    }

    @Override
    public String getAsString() {
        // TODO Auto-generated method stub
        return token;
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public Long getCreatedTime() {
        // TODO Auto-generated method stub
        return createdTime;
    }

    @Override
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public AccessTokenImpl(Long ttl, String token, boolean expired, Long createdTime) {
        super();
        this.ttl = ttl;
        this.token = token;
        this.expired = expired;
        this.createdTime = createdTime;
    }

    
}

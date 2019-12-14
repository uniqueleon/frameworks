package org.aztec.framework.web.security.impl;

import java.util.UUID;

import org.aztec.framework.web.security.Token;

public class SimpleToken implements Token {
    
    private String content;
    private long ttl;
    private boolean isExpired;
    private long timestamp;
    private String id;
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getTtl() {
        return ttl;
    }
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
    public boolean isExpired() {
        return isExpired;
    }
    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }
    public SimpleToken(String content, long timestamp,long ttl, boolean isExpired) {
        super();
        this.content = content;
        this.timestamp = timestamp;
        this.ttl = ttl;
        this.isExpired = isExpired;
        id = UUID.randomUUID().toString();
    }
    
    public SimpleToken(String id,String content, long timestamp,long ttl, boolean isExpired) {
        super();
        this.content = content;
        this.timestamp = timestamp;
        this.ttl = ttl;
        this.isExpired = isExpired;
        this.id = id;
    }
    @Override
    public long getTimestamp() {
        // TODO Auto-generated method stub
        return timestamp;
    }
    @Override
    public String getID() {
        return id;
    }
    @Override
    public String getAsString() {
        return content;
    }
    @Override
    public void setID(String id) {
        this.id = id;
    }

    
}

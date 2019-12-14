package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Service
@Scope("singleton")
@DisconfFile(filename="token-config.properties")
public class SessionEncryptConfig {

    /**
     * 加密新密钥，用于加密session的秘钥
     */
    private String newKey = "";
    /**
     * 加密旧密钥，用于过渡
     */
    private String oldKey = "";
    
    private String mode = "";
    private String ttl = "";
    private String tokenValidEnabled = "false";
    
    private String renewInterval = "20000";
    
    public static final long DEFAULT_TOKEN_TTL = 12 * 60 * 60 * 1000l;
    
    
    public static enum EncryptMode{
        NONE,FORCE,MODERATE;
    }
    
    @DisconfFileItem(name="newkey",associateField="newKey")
    public String getNewKey() {
        return newKey;
    }
    public void setNewKey(String newKey) {
        this.newKey = newKey;
    }
    @DisconfFileItem(name="oldkey",associateField="oldKey")
    public String getOldKey() {
        return oldKey;
    }
    public void setOldKey(String oldKey) {
        this.oldKey = oldKey;
    }
    @DisconfFileItem(name="mode",associateField="mode")
    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }

    @DisconfFileItem(name="ttl",associateField="ttl")
    public String getTtl() {
        return ttl;
    }
    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
    
    @DisconfFileItem(name="renew.interval",associateField="renewInterval")
    public String getRenewInterval() {
        return renewInterval;
    }
    public void setRenewInterval(String renewInterval) {
        this.renewInterval = renewInterval;
    }
    @DisconfFileItem(name="valid.enabled",associateField="tokenValidEnabled")
    public String getTokenValidEnabled() {
        return tokenValidEnabled;
    }
    public void setTokenValidEnabled(String tokenValidEnabled) {
        this.tokenValidEnabled = tokenValidEnabled;
    }
    
    public Long ttlToMilliSec(){
        return timeToMilliSec(getTtl());
    }
    
    public Long renewIntervalToMS(){
        return timeToMilliSec(getRenewInterval());
    }
    
    public Long timeToMilliSec(String ttl){
        if(ttl != null){
            if(ttl.endsWith("ms")){
                String tValue = ttl.substring(0,ttl.length() - 2);
                return Long.parseLong(tValue);
            }else if(ttl.endsWith("s")){
                String tValue = ttl.substring(0,ttl.length() - 1);
                return Long.parseLong(tValue) * 1000;
            }else if(ttl.endsWith("m")){
                String tValue = ttl.substring(0,ttl.length() - 1);
                return Long.parseLong(tValue) * 60 * 1000 ;
            }else if(ttl.endsWith("h")){
                String tValue = ttl.substring(0,ttl.length() - 1);
                return Long.parseLong(tValue) * 60 * 60 * 1000;
            }
        }
        return DEFAULT_TOKEN_TTL;
    }
    
    public EncryptMode getEncryptMode(){
        if(mode != null && !mode.isEmpty()){
            for(EncryptMode mode : EncryptMode.values()){
                if(mode.name().equals(getMode())){
                    return mode;
                }
            }
        }
        return null;
    }
    
    public boolean isTokenValidEnabled(){
        return Boolean.parseBoolean(getTokenValidEnabled());
    }
    
}

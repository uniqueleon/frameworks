package org.aztec.framework.web.security.impl;

import java.util.Map;

import org.aztec.framework.core.SpringApplicationContext;
import org.aztec.framework.core.common.exceptions.BusinessException;
import org.aztec.framework.web.security.Credentials;

import com.google.common.collect.Maps;
import com.sjsc.framework.redis.core.RedisManage;

public class UserDataCredentials implements Credentials{
    
    private String content;
    private String account;
    private Long id;
    private static Map<String,Object> localCache = Maps.newConcurrentMap();
    protected static Map<String,String> userDetailServiceName = Maps.newConcurrentMap();
    protected RedisManage redisManager = RedisManage.getInstance();
    
    public static void registUserDetailService(String dataName,String serviceName){
        userDetailServiceName.put(dataName, serviceName);
    }

    public static final String USER_DATA_REDIS_KEY_PATTERN = "USER_DATA_%s_%s_%s";
    public static enum USER_DATA_KEYS{
        ROLES("roles"),ACL_CODES("aclCodes");
        
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private USER_DATA_KEYS(String name) {
            this.name = name;
        }
        
        
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public <T> T cast(Class<? extends T> clazz) {
        if(clazz.isAssignableFrom(this.getClass())){
            return (T) this;
        }
        return null;
    }
    
    private static final String CONTENT_SEPERATOR = "$$$";

    @Override
    public void parse(String content) throws BusinessException {
        // TODO Auto-generated method stub
        String[] contentArr = content.split("\\$\\$\\$");
        this.id = Long.parseLong(contentArr[0]);
        this.account = contentArr[1];
       
    }
    

    public UserDataCredentials(String account, Long id) {
        super();
        this.account = account;
        this.id = id;
        this.content = id + CONTENT_SEPERATOR + account;
    }

    public UserDataCredentials() {
        super();
    }

    @Override
    public String getAsString() {
        return content;
    }
    
    public String getRedisKey(String dataName){
        return String.format(USER_DATA_REDIS_KEY_PATTERN,id,account,dataName);
    }
    
    public <T> T getUserData(String dataName){
            String redisKey = getRedisKey(dataName);
            if(localCache.containsKey(redisKey)){
                return (T)localCache.get(redisKey);
            }
            else {
                Object redisData = redisManager.get(redisKey);
                if(redisData == null){
                    UserDataFetcher<T> dataService = SpringApplicationContext.getBean(userDetailServiceName.get(dataName));
                    if(dataService != null){
                        redisData = dataService.getData(this);
                        //redisManager.set(redisKey, redisData);
                        //localCache.put(redisKey, redisData);
                    }
                    else{
                        return null;
                    }
                    
                }
                return (T) redisData;
            }
    }
    
    public void evictCache(String dataName){
        String redisKey = getRedisKey(dataName);
        if(localCache.containsKey(redisKey)){
            localCache.remove(redisKey);
        }
        redisManager.del(redisKey);
    }
    
    public static interface UserDataFetcher<T>{
        public T getData(UserDataCredentials credentials);
    }
    
}

package org.aztec.framework.disconf.items;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfItem;

@Service(DisconfItemBeanNames.REDIS_DISCONFIG_BEAN_NAME)
@Scope("singleton")
public class RedisConfig {

    private String content;
    
    @Value("${sjsc.framework.redis.docker_env}")
    private String dockerEnv;

    @DisconfItem(key="redis.conf",associateField="content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDockerEnv() {
        return dockerEnv;
    }

    public void setDockerEnv(String dockerEnv) {
        this.dockerEnv = dockerEnv;
    }
    
    
}

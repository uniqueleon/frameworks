package org.aztec.framework.core.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;

/**
 * 健康检查类，用于注入到eureka客户端中，以进行一些自定义的健康检查工作
 * @author 01390615
 *
 */
@Component
public class ServerHealthChecker implements HealthCheckHandler {

    @Autowired
    HealthService appService;
    
    private static final Logger LOG = LoggerFactory.getLogger(ServerHealthChecker.class);
    @Override
    public InstanceStatus getStatus(InstanceStatus currentStatus) {
        // TODO Auto-generated method stub
        
        try {
            LOG.debug("CHECKING HEALTH In heartbeat!");
            appService.touch();
            LOG.debug(" HEALTH  CHECK FINISHED !");
            return InstanceStatus.UP;
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            return InstanceStatus.DOWN;
        }
    }

}

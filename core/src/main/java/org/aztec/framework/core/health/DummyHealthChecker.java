package org.aztec.framework.core.health;

import org.springframework.stereotype.Component;

/**
 * 默认健康检查实现类，如果程序不另外实现{@link HealthService}，将调用此实现。
 * @author 01390615
 *
 */
@Component
public class DummyHealthChecker implements HealthService {

    @Override
    public void touch() {
        
    }

}

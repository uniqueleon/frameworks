package org.aztec.framework.core.health;

/**
 * 
 * 用于Eureka客户端启动的时候，进行安全自检。一般来说，Eureka客户端在程 序还没有启动完成时，会调用健康检查模块，来确定当前模块是否已经正常启动。
 * 在默认的情况下，Eureka客户端总是认为程序能够正常启动。但事实上，有些异常是没法在程序启动阶段就能监测出来。（e.g.一些冷启动的模块。如Redis）。
 * 本接口的意义就在于覆盖原来的默认配置，来进行一些健康检查的工作，以免将不可用的实例注册到注册中心中.
 * 
 * @author 01390615
 *
 */
public interface HealthService {

    /**
     * 激活健康检查
     * @throws Exception
     */
    public void touch() throws Exception;
}

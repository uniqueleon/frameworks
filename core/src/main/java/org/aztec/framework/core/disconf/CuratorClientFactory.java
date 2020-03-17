package org.aztec.framework.core.disconf;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorClientFactory implements PoolableObjectFactory<CuratorFramework>{
    
    DisconfConnectionConfig config;

    public CuratorClientFactory(DisconfConnectionConfig config) {
        super();
        this.config = config;
    }

    @Override
    public CuratorFramework makeObject() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(config.getZkConnectUrl(), retryPolicy);
        client.start();
        return client;
    }

    @Override
    public void destroyObject(CuratorFramework obj) throws Exception {
        
    }

    @Override
    public boolean validateObject(CuratorFramework obj) {
        return true;
    }

    @Override
    public void activateObject(CuratorFramework obj) throws Exception {
    }

    @Override
    public void passivateObject(CuratorFramework obj) throws Exception {
    }

}

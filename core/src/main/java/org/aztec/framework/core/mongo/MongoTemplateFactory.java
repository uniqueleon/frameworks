package org.aztec.framework.core.mongo;

import org.apache.commons.pool.PoolableObjectFactory;
import org.aztec.framework.core.MongoDBAutoConfiguration;
import org.aztec.framework.disconf.items.MongoDBConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;

public class MongoTemplateFactory implements PoolableObjectFactory<MongoTemplate> {
    MongoClient client;
    private MongoDBConf mongoConf;

    private static final Logger LOG = LoggerFactory.getLogger(MongoDBAutoConfiguration.class);

    public MongoTemplateFactory(MongoClient client,MongoDBConf conf) {
        super();
        this.client = client;
        this.mongoConf = conf;
    }

    @Override
    public MongoTemplate makeObject() throws Exception {
        String connectUrl = mongoConf.getConnectionUrl();
        LOG.info(MongoDBAutoConfiguration.LOG_PREFIX + "Connection Url=" + connectUrl);
        String database = connectUrl.substring(connectUrl.lastIndexOf("/") + 1,connectUrl.length());
        return new MongoTemplate(client, database);
    }

    @Override
    public void destroyObject(MongoTemplate obj) throws Exception {
        // TODO Auto-generated method stub
        //client.close();
    }

    @Override
    public boolean validateObject(MongoTemplate obj) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void activateObject(MongoTemplate obj) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void passivateObject(MongoTemplate obj) throws Exception {
        // TODO Auto-generated method stub
        
    }

}

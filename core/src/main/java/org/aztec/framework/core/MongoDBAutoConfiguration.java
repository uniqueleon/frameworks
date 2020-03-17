package org.aztec.framework.core;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.aztec.framework.core.mongo.MongoTemplateFactory;
import org.aztec.framework.disconf.items.GlobalMongoConf;
import org.aztec.framework.disconf.items.MongoDBConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@ConditionalOnProperty(prefix="org.aztec.framework",name="mongodb.disconf_enabled",havingValue="true")
@ConditionalOnBean({MongoDBConf.class})
public class MongoDBAutoConfiguration {

    @Autowired
    private MongoDBConf mongoConf;
    
    @Autowired
    private GlobalMongoConf globalConf;
    
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBAutoConfiguration.class);
    
    public final static String LOG_PREFIX = "[MONGO_FRAMEWORK]:";
    
    private static GenericObjectPool<MongoTemplate> templatePool;
    //private static GenericObjectPool<MongoClient> clientPool;
    private static MongoTemplateFactory factory;
    
    private static AtomicBoolean initialzied = new AtomicBoolean(false);
    

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoConf.getConnectionUrl());
    }
    
    @Bean
    public MongoTemplate mongoTemplate(@Qualifier("mongoClient")MongoClient client) throws Exception{
        if (!initialzied.get() && initialzied.compareAndSet(false, true)) {
            factory = new MongoTemplateFactory(client, mongoConf);
            templatePool = new GenericObjectPool<MongoTemplate>(factory);
            for (int i = 0; i < Integer.parseInt(globalConf.getPoolSize()); i++) {
                templatePool.addObject();
            }
        }
        MongoTemplate template = Boolean.parseBoolean(globalConf.getUsePool()) ? getTemplateFromPool() : makeTemplateDirectly(client);
        if (!initialzied.get() && initialzied.compareAndSet(false, true)) {
            test(template);
        }
        return template;
    }
    
    private MongoTemplate getTemplateFromPool() throws Exception{
        return templatePool.borrowObject();
    }
    
    private MongoTemplate makeTemplateDirectly(MongoClient client){
        String connectUrl = mongoConf.getConnectionUrl();
        LOG.info(MongoDBAutoConfiguration.LOG_PREFIX + "Connection Url=" + connectUrl);
        String database = connectUrl.substring(connectUrl.lastIndexOf("/") + 1,connectUrl.length());
        MongoTemplate template = new MongoTemplate(client, database);
        return template;
    }
    
    @Document(collection="test")
    private static class TestObject{
        
        private Long id;
        private String content;
        private Long insertUseTime;
        public TestObject() {
            super();
            this.id = RandomUtils.nextLong();
            this.content = "Only for Test";
        }
        public void setInsertUseTime(Long insertUseTime) {
            this.insertUseTime = insertUseTime;
        }
        
        
    }
    
    private void test(MongoTemplate template){
        Long curTime = System.currentTimeMillis();
        TestObject testObj = new TestObject();
        template.insert(testObj);
        Long useTime = System.currentTimeMillis() - curTime;
        testObj.setInsertUseTime(useTime);
        template.save(testObj);
        LOG.info(LOG_PREFIX + " insert use:" + useTime +"ms,total use time:" + (System.currentTimeMillis() - curTime)); 
    }
}

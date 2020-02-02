package org.aztec.framework.core;

import org.aztec.framework.disconf.items.MongoDBConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@ConditionalOnProperty(prefix="org.aztec.framework",name="mongodb.disconf_enabled",havingValue="true")
@ConditionalOnBean({MongoDBConf.class})
public class MongoDBAutoConfiguration {

    @Autowired
    private MongoDBConf mongoConf;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoConf.getConnectionUrl());
    }
    
    @Bean
    public MongoTemplate mongoTemplate(@Qualifier("mongoClient")MongoClient client){
        String connectUrl = mongoConf.getConnectionUrl();
        String database = connectUrl.substring(connectUrl.lastIndexOf("/") + 1,connectUrl.length());
        return new MongoTemplate(client, database);
    }
}

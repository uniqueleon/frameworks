package org.aztec.framework.core.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.BasicBSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

//@Component
public class MongoUtils {

    //@Autowired
    MongoTemplate template;
    
    public <T> Page<T> findByPage(Class<T> entityCls,String collectionName,BasicBSONObject sample,int pageNo,int pageSize){
        
        long total = 0l;
        List<T> content = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.byExample(sample));
        total = template.count(query, entityCls);
        template.find(query, entityCls);
        //template.getco
        return new PageImpl<>(content, null, total);
    }
}

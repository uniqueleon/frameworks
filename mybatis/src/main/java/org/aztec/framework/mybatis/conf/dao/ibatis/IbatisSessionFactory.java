package org.aztec.framework.mybatis.conf.dao.ibatis;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.aztec.framework.mybatis.conf.dao.ibatis.plugin.EntityOperatorInterceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@org.springframework.context.annotation.Configuration

public class IbatisSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger("BASIC");

    @Bean(name = "sessionFactory")
    public SqlSessionFactory sessionFactory(@Qualifier("dataSource") DataSource dataSource) {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // 添加XML目录

        try {
            Resource[] resources = getPathMatcherResources();
            bean.setMapperLocations(resources);
            Interceptor[] plugins = new Interceptor[] { new EntityOperatorInterceptor() };
            bean.setPlugins(plugins);
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "sqlTempl")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sessionFactory") SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    public Resource[] getPathMatcherResources() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:sqlmap/*-mapper.xml");
        return resources;
    }

}

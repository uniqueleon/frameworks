package org.aztec.framework.mybatis.conf.dao.ibatis.mapper;

import java.util.Properties;

import org.aztec.framework.mybatis.conf.dao.ibatis.IbatisSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;

@Configuration
@AutoConfigureAfter(IbatisSessionFactory.class)
public class MyBatisMapperScannerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(
            @Qualifier("mapperScanConf") MybatisMapperConfig mapperScanConf) {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sessionFactory");
        mapperScannerConfigurer.setBasePackage(mapperScanConf.getMapperScanPackage());
        Properties properties = new Properties();
        // 这里要特别注意，不要把MyMapper放到 basePackage 中，也就是不能同其他Mapper一样被扫描到。
        properties.setProperty("mappers", mapperScanConf.getMapperCls().getName());
        properties.setProperty("notEmpty", mapperScanConf.notEmpty());
        properties.setProperty("IDENTITY", mapperScanConf.identity());
        mapperScannerConfigurer.setProperties(properties);
        return mapperScannerConfigurer;
    }

}

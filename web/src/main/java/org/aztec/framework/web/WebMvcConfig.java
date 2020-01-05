package org.aztec.framework.web;

import org.aztec.framework.disconf.items.SessionEncryptConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sjsc.framework.web.interceptor.EntrypointLogInterceptor;
import com.sjsc.framework.web.interceptor.TokenValidateInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    SessionEncryptConfig encryptConfig;
    @Autowired
    TokenValidateInterceptor tokenInterceptor;
    @Autowired
    EntrypointLogInterceptor logInterceptor;
    @Autowired(required=false)
    CustomerInterceptorRegistry interceptRegistry;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TODO Auto-generated method stub
        if(encryptConfig.isTokenValidEnabled()){
            registry.addInterceptor(tokenInterceptor);
            registry.addInterceptor(logInterceptor);
        }
        if(interceptRegistry != null){
            for(HandlerInterceptor interceptor : interceptRegistry.getCustomerInterceptors()){
                registry.addInterceptor(interceptor);
            }
        }
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    /*@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(jackson2HttpMessageConverter());
    }*/

    /**
     * 时间格式转换器,将Date类型统一转换为yyyy-MM-dd HH:mm:ss格式的字符串
     * @author 
     * @date 2017年9月10日上午9:33:06
     * @return
     */
    /*@Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();

        
        //Long类型转String类型
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(simpleModule);
        
        converter.setObjectMapper(mapper);
        return converter;
    }*/ 

        
}
package org.aztec.framework.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContext implements ApplicationContextAware,BeanFactoryAware{
    
    private static BeanFactory beanFactory;
    private static ApplicationContext context;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // TODO Auto-generated method stub
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        // TODO Auto-generated method stub
        context = arg0;
    }
    
    public static <T> T getBean(String beanName){
        if(beanFactory == null){
            return null;
        }
        return (T) beanFactory.getBean(beanName);
    }

}

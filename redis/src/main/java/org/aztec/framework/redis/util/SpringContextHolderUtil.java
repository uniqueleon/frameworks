package org.aztec.framework.redis.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class SpringContextHolderUtil implements ApplicationContextAware{

	private static ApplicationContext ac;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ac = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext(){  
	      return ac;  
	} 
	public static boolean containsBeanName(String beanname){  
		String[] names=ac.getBeanDefinitionNames();
		for(String name:names){
			if(name.contains(beanname)){
				return name.contains(beanname);
			}
		}
		return false;
	} 
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name){
		return (T) ac.getBean(name);
	}
	public static void main(String[] args) {
		System.out.println("simple bean"+JSON.toJSON(ac.getBean("simple")));
	}

}


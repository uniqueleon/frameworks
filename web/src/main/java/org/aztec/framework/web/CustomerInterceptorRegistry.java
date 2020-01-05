package org.aztec.framework.web;

import java.util.List;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 自定义拦截器
 * @author 01390615
 *
 */
public interface CustomerInterceptorRegistry {

    /**
     * 读取用户自定义拦截器
     * @return
     */
    public List<HandlerInterceptor> getCustomerInterceptors();
}

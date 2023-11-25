package com.yww.api.config;

import com.yww.api.interceptor.ApiMethodInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * <p>
 *      注册拦截器
 * </p>
 *
 * @author yww
 * @since 2023/11/26
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Lazy
    @Resource
    private ApiMethodInterceptor apiMethodInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiMethodInterceptor).addPathPatterns("/api/v1/**");
    }

}

package com.turtle.config;

import com.turtle.interceptor.jwtTokenUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;


/**
 * SpringMVC相关配置
 */
@Slf4j
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private jwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .addPathPatterns("/soup/**") // 拦截所有soup相关方法
                .addPathPatterns("/game/**")  // game相关的功能需要登录
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/soup/list") // 查看题目列表不需要用户权限
                .excludePathPatterns("/soup/*"); // 查看题目详情不需要用户权限

    }
}

package com.xxxx.crm.config;

import com.xxxx.crm.interceptors.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

// @Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    public NoLoginInterceptor ctreateNoLoginInterceptor(){
        return new NoLoginInterceptor();
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ctreateNoLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/index","/user/login","/css/**","/images/**","/js/**","/lib/**");
    }
}

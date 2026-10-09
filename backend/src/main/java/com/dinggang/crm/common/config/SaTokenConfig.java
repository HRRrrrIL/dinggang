package com.dinggang.crm.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 路由拦截配置
 * 配置哪些路径需要登录校验，哪些放行
 *
 * @author dinggang
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token登录拦截器
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",          // 登录接口
                        "/auth/captcha",        // 验证码
                        "/portal/**",           // C端H5（独立令牌鉴权）
                        "/doc.html",            // API文档
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico"
                );
    }
}

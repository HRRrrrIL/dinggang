package com.dinggang.crm.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.dinggang.crm.common.security.CustomDataPermissionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 注册分页插件、数据权限拦截器
 *
 * @author dinggang
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 数据权限拦截器（放在分页之前）
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(new CustomDataPermissionHandler()));

        // 分页拦截器
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(500L);
        interceptor.addInnerInterceptor(pagination);

        return interceptor;
    }
}

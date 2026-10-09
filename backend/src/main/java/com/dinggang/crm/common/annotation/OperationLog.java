package com.dinggang.crm.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标注在Controller方法上，自动记录操作日志
 *
 * @author dinggang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     */
    String action() default "";
}

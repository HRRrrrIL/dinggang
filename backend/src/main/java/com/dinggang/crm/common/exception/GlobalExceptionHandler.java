package com.dinggang.crm.common.exception;

import com.dinggang.crm.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一捕获Controller层抛出的异常，转换为标准响应格式
 *
 * @author dinggang
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public Result<Void> handleBindException(org.springframework.validation.BindException e) {
        String msg = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.warn("参数校验异常: {}", msg);
        return Result.error(400, msg);
    }

    /**
     * 权限异常 (Sa-Token)
     */
    @ExceptionHandler(cn.dev33.satoken.exception.NotLoginException.class)
    public Result<Void> handleNotLoginException(cn.dev33.satoken.exception.NotLoginException e) {
        log.warn("未登录: {}", e.getMessage());
        return Result.error(401, "未登录或登录已过期");
    }

    @ExceptionHandler(cn.dev33.satoken.exception.NotPermissionException.class)
    public Result<Void> handleNotPermissionException(cn.dev33.satoken.exception.NotPermissionException e) {
        log.warn("无权限: {}", e.getMessage());
        return Result.error(403, "无权限访问: " + e.getPermission());
    }

    /**
     * 其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统内部错误，请联系管理员");
    }
}

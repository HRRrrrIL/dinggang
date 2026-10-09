package com.dinggang.crm.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于抛出可预期的业务错误，由全局异常处理器统一捕获
 *
 * @author dinggang
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}

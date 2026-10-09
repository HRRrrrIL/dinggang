package com.dinggang.crm.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.dinggang.crm.common.annotation.OperationLog;
import com.dinggang.crm.modules.system.entity.SysOperationLog;
import com.dinggang.crm.modules.system.mapper.SysOperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 操作日志AOP切面
 * 拦截标注了@OperationLog的方法，自动记录操作日志
 *
 * @author dinggang
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperationLogMapper logMapper;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperationLog operationLogEntity = new SysOperationLog();

        try {
            // 填充操作人信息
            try {
                if (StpUtil.isLogin()) {
                    operationLogEntity.setUserId(StpUtil.getLoginIdAsLong());
                    operationLogEntity.setUsername(StpUtil.getLoginIdAsString());
                }
            } catch (Exception ignored) {}

            // 填充请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLogEntity.setIp(request.getRemoteAddr());
                operationLogEntity.setUa(request.getHeader("User-Agent"));
            }

            // 填充注解信息
            operationLogEntity.setModule(operationLog.module());
            operationLogEntity.setAction(operationLog.action());
            operationLogEntity.setResult(1);

            // 执行目标方法
            Object result = point.proceed();
            return result;

        } catch (Throwable e) {
            operationLogEntity.setResult(0);
            operationLogEntity.setDetail(e.getMessage());
            throw e;
        } finally {
            operationLogEntity.setCostMs(System.currentTimeMillis() - startTime);
            try {
                logMapper.insert(operationLogEntity);
            } catch (Exception e) {
                log.error("操作日志写入失败", e);
            }
        }
    }
}

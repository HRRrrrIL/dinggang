package com.dinggang.crm.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.system.entity.SysOperationLog;
import com.dinggang.crm.modules.system.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志查询控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final SysOperationLogMapper logMapper;

    /**
     * 分页查询操作日志
     */
    @GetMapping
    public Result<PageResult<SysOperationLog>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String username) {

        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(module != null && !module.isEmpty(), SysOperationLog::getModule, module);
        wrapper.like(username != null && !username.isEmpty(), SysOperationLog::getUsername, username);
        wrapper.orderByDesc(SysOperationLog::getCreateTime);

        Page<SysOperationLog> result = logMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), page, size));
    }
}

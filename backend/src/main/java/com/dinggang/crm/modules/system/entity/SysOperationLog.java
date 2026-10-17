package com.dinggang.crm.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 *
 * @author dinggang
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 操作人账号
     */
    private String username;

    /**
     * 模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private String bizId;

    /**
     * 操作详情
     */
    private String detail;

    /**
     * 请求参数
     */
    private String params;

    /**
     * IP地址
     */
    private String ip;

    /**
     * User-Agent
     */
    private String ua;

    /**
     * 结果: 1成功 0失败
     */
    private Integer result;

    /**
     * 耗时(毫秒)
     */
    private Long costMs;

    /**
     * 操作时间
     */
    private LocalDateTime createTime;
}

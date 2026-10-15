package com.dinggang.crm.modules.serviceorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 售后工单实体
 *
 * @author dinggang
 */
@Data
@TableName("service_order")
public class ServiceOrder implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工单编号: WD-YYYYMM-0001
     */
    private String orderNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 联系人姓名快照
     */
    private String contactName;

    /**
     * 关联合同ID
     */
    private Long contractId;

    /**
     * 类型: 1履约问题 2产品故障 3服务咨询 4投诉建议 5其他
     */
    private Integer type;

    /**
     * 紧急度: 1普通 2紧急 3特急
     */
    private Integer urgency;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 提交人类型: 1内部员工 2客户本人
     */
    private Integer submitterType;

    /**
     * 提交人姓名
     */
    private String submitterName;

    /**
     * 负责人ID
     */
    private Long assigneeId;

    /**
     * 状态: 1待分配 2处理中 3待客户确认 4已闭环 5已驳回
     */
    private Integer status;

    /**
     * 分配时间
     */
    private LocalDateTime assignTime;

    /**
     * 首次响应时间
     */
    private LocalDateTime firstResponseTime;

    /**
     * 响应截止时间
     */
    private LocalDateTime slaDeadline;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 处理结果
     */
    private String resolveDesc;

    /**
     * 驳回原因
     */
    private String rejectReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer delFlag;
}

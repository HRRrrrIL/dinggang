package com.dinggang.crm.modules.performance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 履约节点实体
 *
 * @author dinggang
 */
@Data
@TableName("contract_node")
public class ContractNode implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 来源模板节点ID
     */
    private Long templateNodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 类型: 1付款 2交付 3其他
     */
    private Integer nodeType;

    /**
     * 责任人ID
     */
    private Long ownerId;

    /**
     * 责任部门ID
     */
    private Long deptId;

    /**
     * 截止日期
     */
    private LocalDate dueDate;

    /**
     * 完成标准
     */
    private String finishStandard;

    /**
     * 状态: 0待执行 1进行中 2已完成 3已逾期
     */
    private Integer status;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 是否按时: 1按时 0逾期
     */
    private Integer isOnTime;

    /**
     * 逾期天数
     */
    private Integer overdueDays;

    /**
     * 排序
     */
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

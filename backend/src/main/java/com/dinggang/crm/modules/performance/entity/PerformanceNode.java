package com.dinggang.crm.modules.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("performance_node")
public class PerformanceNode implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型: 1付款 2交付 3其他
     */
    private Integer nodeType;

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
     * 实际完成描述
     */
    private String finishDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

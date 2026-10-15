package com.dinggang.crm.modules.serviceorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 满意度评分实体
 *
 * @author dinggang
 */
@Data
@TableName("satisfaction_score")
public class SatisfactionScore implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工单ID
     */
    private Long orderId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 负责人ID
     */
    private Long assigneeId;

    /**
     * 评分: 1-5
     */
    private Integer score;

    /**
     * 评价维度JSON
     */
    private String dimensionJson;

    /**
     * 具体建议
     */
    private String suggestion;

    /**
     * 评分时间
     */
    private LocalDateTime createTime;
}

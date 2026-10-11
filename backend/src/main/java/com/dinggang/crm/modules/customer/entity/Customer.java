package com.dinggang.crm.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户实体
 *
 * @author dinggang
 */
@Data
@TableName("customer")
public class Customer implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户编号: C-YYYYMM-0001
     */
    private String customerNo;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 层级: 1潜在 2意向 3成交 4复购
     */
    private Integer level;

    /**
     * 层级模式: 1自动 2手动
     */
    private Integer levelMode;

    /**
     * 行业(冗余自标签，便于筛选)
     */
    private String industry;

    /**
     * 客户来源
     */
    private String source;

    /**
     * 需求描述
     */
    private String demandDesc;

    /**
     * 客户偏好
     */
    private String preference;

    /**
     * 意向度: 1-5
     */
    private Integer intentDegree;

    /**
     * 归属销售ID
     */
    private Long ownerId;

    /**
     * 状态: 0流失 1跟进中
     */
    private Integer status;

    /**
     * 最近跟进时间
     */
    private LocalDateTime lastFollowTime;

    /**
     * 备注
     */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer delFlag;
}

package com.dinggang.crm.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户标签实体
 *
 * @author dinggang
 */
@Data
@TableName("customer_tag")
public class CustomerTag implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 类型: INDUSTRY行业类型 / DEMAND需求方向 / INTENT合作意向度 / COOP合作偏好
     */
    private String tagType;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态: 0禁用 1正常
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

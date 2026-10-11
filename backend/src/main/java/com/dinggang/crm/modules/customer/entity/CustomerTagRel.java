package com.dinggang.crm.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户标签关联实体
 *
 * @author dinggang
 */
@Data
@TableName("customer_tag_rel")
public class CustomerTagRel implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 标签ID
     */
    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

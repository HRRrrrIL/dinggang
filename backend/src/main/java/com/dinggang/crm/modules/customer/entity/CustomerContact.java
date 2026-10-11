package com.dinggang.crm.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户联系人实体
 *
 * @author dinggang
 */
@Data
@TableName("customer_contact")
public class CustomerContact implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 职位
     */
    private String position;

    /**
     * 手机号密文(AES-GCM)
     */
    private String phoneCipher;

    /**
     * 手机号脱敏展示: 139****4567
     */
    private String phoneMask;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否主要联系人: 0否 1是
     */
    private Integer isPrimary;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

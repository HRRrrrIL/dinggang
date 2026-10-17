package com.dinggang.crm.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 *
 * @author dinggang
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码(BCrypt)
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号密文
     */
    private String phoneCipher;

    /**
     * 手机号脱敏展示
     */
    private String phoneMask;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 所属部门ID
     */
    private Long deptId;

    /**
     * 直属上级ID
     */
    private Long managerId;

    /**
     * 状态: 0禁用 1正常
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer delFlag;
}

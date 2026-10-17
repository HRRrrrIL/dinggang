package com.dinggang.crm.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色实体
 *
 * @author dinggang
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色编码: SALE/LEGAL/OPS/FOUNDER/ADMIN
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 数据范围: 1全部 2本部门 3本人
     */
    private Integer dataScope;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态: 0禁用 1正常
     */
    private Integer status;
}

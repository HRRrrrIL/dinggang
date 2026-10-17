package com.dinggang.crm.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限点实体
 *
 * @author dinggang
 */
@Data
@TableName("sys_permission")
public class SysPermission implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限编码: 如 contract:approve
     */
    private String permCode;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 所属模块
     */
    private String module;

    /**
     * 类型: 1菜单 2按钮 3接口
     */
    private Integer type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态: 0禁用 1正常
     */
    private Integer status;
}

package com.dinggang.crm.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典项实体
 *
 * @author dinggang
 */
@Data
@TableName("sys_dict_item")
public class SysDictItem implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典值
     */
    private String itemValue;

    /**
     * 字典标签
     */
    private String itemLabel;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态: 0禁用 1正常
     */
    private Integer status;
}

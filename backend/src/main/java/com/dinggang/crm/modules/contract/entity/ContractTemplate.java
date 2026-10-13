package com.dinggang.crm.modules.contract.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 合同模板实体
 *
 * @author dinggang
 */
@Data
@TableName("contract_template")
public class ContractTemplate implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 分类: SALE/PURCHASE/SERVICE/FRAME
     */
    private String category;

    /**
     * 适用场景描述
     */
    private String sceneDesc;

    /**
     * 条款结构JSON（编辑器数据源）
     */
    private String contentJson;

    /**
     * HTML渲染内容
     */
    private String contentHtml;

    /**
     * 状态: 0禁用 1启用
     */
    private Integer status;

    /**
     * 版本号
     */
    private String version;

    /**
     * 使用次数
     */
    private Integer useCount;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer delFlag;
}

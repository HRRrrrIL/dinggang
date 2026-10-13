package com.dinggang.crm.modules.contract.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 合同模板节点清单实体
 * 用于合同生效后自动拆分履约节点
 *
 * @author dinggang
 */
@Data
@TableName("contract_template_node")
public class ContractTemplateNode implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 节点名称: 预付款支付/产品交付
     */
    private String nodeName;

    /**
     * 类型: 1付款 2交付 3其他
     */
    private Integer nodeType;

    /**
     * 相对生效日偏移天数（如预付款=0，交付=30）
     */
    private Integer offsetDays;

    /**
     * 默认责任角色: SALE/OPS/TECH
     */
    private String defaultOwnerRole;

    /**
     * 完成标准
     */
    private String finishStandard;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态: 0禁用 1启用
     */
    private Integer status;
}

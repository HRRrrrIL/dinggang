package com.dinggang.crm.modules.contract.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同实体
 *
 * @author dinggang
 */
@Data
@TableName("contract")
public class Contract implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同编号: HT-YYYYMMDD-0001
     */
    private String contractNo;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 合同类型: SALE/PURCHASE/SERVICE/FRAME/OTHER
     */
    private String contractType;

    /**
     * 金额密文(AES-GCM)
     */
    private byte[] amountCipher;

    /**
     * 是否有金额: 0无 1有
     */
    private Integer amountFlag;

    /**
     * 币种
     */
    private String currency;

    /**
     * 签订日期
     */
    private LocalDate signDate;

    /**
     * 生效日期
     */
    private LocalDate startDate;

    /**
     * 到期日期
     */
    private LocalDate endDate;

    /**
     * 条款结构JSON
     */
    private String contentJson;

    /**
     * HTML渲染缓存
     */
    private String contentHtml;

    /**
     * 状态: 1草稿 2审批中 3已生效 4已驳回 5已归档
     */
    private Integer status;

    /**
     * 归属销售ID
     */
    private Long ownerId;

    /**
     * Flowable流程实例ID
     */
    private String processInstanceId;

    /**
     * 成品PDF对象键
     */
    private String pdfObjectKey;

    /**
     * PDF状态: 0未生成 1生成中 2成功 3失败
     */
    private Integer pdfStatus;

    /**
     * 归档时间
     */
    private LocalDateTime archiveTime;

    /**
     * 归档人
     */
    private Long archiveBy;

    /**
     * 内容版本号
     */
    private Integer version;

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

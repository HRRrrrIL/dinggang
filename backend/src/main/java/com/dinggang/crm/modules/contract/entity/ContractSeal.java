package com.dinggang.crm.modules.contract.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 合同签章实体
 *
 * @author dinggang
 */
@Data
@TableName("contract_seal")
public class ContractSeal implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 签章类型: 1甲方公章 2乙方公章 3法人签字
     */
    private Integer sealType;

    /**
     * 签章人ID
     */
    private Long sealUserId;

    /**
     * 签章人姓名
     */
    private String sealUserName;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * X坐标(%)
     */
    private Integer posX;

    /**
     * Y坐标(%)
     */
    private Integer posY;

    /**
     * 状态: 0待盖章 1已盖章 2已撤销
     */
    private Integer status;

    /**
     * 签章时间
     */
    private LocalDateTime createTime;
}

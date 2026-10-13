package com.dinggang.crm.modules.contract.service;

import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.modules.contract.entity.Contract;

import java.util.Map;

/**
 * 合同服务接口
 *
 * @author dinggang
 */
public interface ContractService {

    /**
     * 分页查询合同列表
     */
    PageResult<Contract> page(long page, long size, String contractNo, String contractName,
                                Long customerId, String contractType, Integer status, Long ownerId);

    /**
     * 获取合同详情（含审批记录、签章、节点）
     */
    Map<String, Object> getContractDetail(Long id);

    /**
     * 基于模板创建合同（草稿）
     */
    Contract createContract(Contract contract);

    /**
     * 更新合同（草稿/已驳回状态可改）
     */
    Contract updateContract(Long id, Contract contract);

    /**
     * 提交审批
     */
    void submitApproval(Long id);

    /**
     * 审批合同（通过/驳回）
     *
     * @param id      合同ID
     * @param pass    true通过 false驳回
     * @param comment 审批意见
     */
    void approveContract(Long id, boolean pass, String comment);

    /**
     * 归档合同
     */
    void archiveContract(Long id);
}

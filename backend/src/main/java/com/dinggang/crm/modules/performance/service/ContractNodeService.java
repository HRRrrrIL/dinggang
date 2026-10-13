package com.dinggang.crm.modules.performance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dinggang.crm.modules.performance.entity.ContractNode;

import java.util.List;

/**
 * 履约节点服务接口
 *
 * @author dinggang
 */
public interface ContractNodeService extends IService<ContractNode> {

    /**
     * 按合同ID查询节点列表
     */
    List<ContractNode> listByContractId(Long contractId);

    /**
     * 合同生效后，根据模板节点清单自动生成履约节点
     *
     * @param contractId   合同ID
     * @param templateId   模板ID
     * @param startDate    合同生效日期
     */
    void autoGenerateNodes(Long contractId, Long templateId, java.time.LocalDate startDate);

    /**
     * 手动添加节点
     */
    ContractNode addNode(ContractNode node);

    /**
     * 完成节点
     */
    void finishNode(Long id);

    /**
     * 每日定时任务：扫描即将到期和已逾期的节点，发送提醒
     */
    void scanAndRemind();
}

package com.dinggang.crm.modules.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dinggang.crm.common.exception.BusinessException;
import com.dinggang.crm.modules.contract.entity.ContractTemplateNode;
import com.dinggang.crm.modules.contract.mapper.ContractTemplateNodeMapper;
import com.dinggang.crm.modules.performance.entity.ContractNode;
import com.dinggang.crm.modules.performance.mapper.ContractNodeMapper;
import com.dinggang.crm.modules.performance.service.ContractNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 履约节点服务实现
 *
 * @author dinggang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractNodeServiceImpl extends ServiceImpl<ContractNodeMapper, ContractNode> implements ContractNodeService {

    private final ContractTemplateNodeMapper templateNodeMapper;

    @Override
    public List<ContractNode> listByContractId(Long contractId) {
        return this.list(
                new LambdaQueryWrapper<ContractNode>()
                        .eq(ContractNode::getContractId, contractId)
                        .orderByAsc(ContractNode::getSort)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoGenerateNodes(Long contractId, Long templateId, LocalDate startDate) {
        if (templateId == null) {
            log.info("合同{}未关联模板，跳过自动生成节点", contractId);
            return;
        }

        // 查询模板的节点清单
        List<ContractTemplateNode> templateNodes = templateNodeMapper.selectList(
                new LambdaQueryWrapper<ContractTemplateNode>()
                        .eq(ContractTemplateNode::getTemplateId, templateId)
                        .eq(ContractTemplateNode::getStatus, 1)
                        .orderByAsc(ContractTemplateNode::getSort)
        );

        for (ContractTemplateNode tNode : templateNodes) {
            ContractNode node = new ContractNode();
            node.setContractId(contractId);
            node.setTemplateNodeId(tNode.getId());
            node.setNodeName(tNode.getNodeName());
            node.setNodeType(tNode.getNodeType());
            node.setDueDate(startDate.plusDays(tNode.getOffsetDays() != null ? tNode.getOffsetDays() : 0));
            node.setFinishStandard(tNode.getFinishStandard());
            node.setStatus(0);  // 待执行
            node.setSort(tNode.getSort());
            this.save(node);
        }

        log.info("合同{}自动生成{}个履约节点", contractId, templateNodes.size());
    }

    @Override
    public ContractNode addNode(ContractNode node) {
        node.setStatus(0);
        node.setIsOnTime(0);
        node.setOverdueDays(0);
        this.save(node);
        return node;
    }

    @Override
    public void finishNode(Long id) {
        ContractNode node = this.getById(id);
        if (node == null) {
            throw new BusinessException("节点不存在");
        }

        ContractNode update = new ContractNode();
        update.setId(id);
        update.setStatus(2);  // 已完成
        update.setFinishTime(LocalDateTime.now());
        // 判断是否按时完成
        if (node.getDueDate() != null) {
            update.setIsOnTime(!LocalDate.now().isAfter(node.getDueDate()) ? 1 : 0);
        }
        this.updateById(update);
    }

    @Override
    public void scanAndRemind() {
        LocalDate today = LocalDate.now();

        // 1. 扫描已逾期节点（状态=待执行/进行中，截止日期<今天）
        List<ContractNode> overdueNodes = this.list(
                new LambdaQueryWrapper<ContractNode>()
                        .in(ContractNode::getStatus, 0, 1)
                        .lt(ContractNode::getDueDate, today)
        );

        for (ContractNode node : overdueNodes) {
            long overdueDays = today.toEpochDay() - node.getDueDate().toEpochDay();

            // 更新逾期状态
            ContractNode update = new ContractNode();
            update.setId(node.getId());
            update.setStatus(3);  // 已逾期
            update.setOverdueDays((int) overdueDays);
            this.updateById(update);

            // TODO: 发送提醒（系统消息 + 邮件）
            log.warn("节点[{}]已逾期{}天，责任人:{}", node.getNodeName(), overdueDays, node.getOwnerId());
        }

        // 2. 扫描3天内即将到期的节点
        LocalDate soon = today.plusDays(3);
        List<ContractNode> soonNodes = this.list(
                new LambdaQueryWrapper<ContractNode>()
                        .in(ContractNode::getStatus, 0, 1)
                        .ge(ContractNode::getDueDate, today)
                        .le(ContractNode::getDueDate, soon)
        );

        for (ContractNode node : soonNodes) {
            // TODO: 发送即将到期提醒
            log.info("节点[{}]将于{}到期", node.getNodeName(), node.getDueDate());
        }

        log.info("履约节点扫描完成: 逾期{}个, 即将到期{}个", overdueNodes.size(), soonNodes.size());
    }
}

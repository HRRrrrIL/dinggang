package com.dinggang.crm.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dinggang.crm.modules.performance.entity.PerformanceNode;
import com.dinggang.crm.modules.performance.mapper.PerformanceNodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 履约节点定时任务
 * 每天早上9点扫描即将到期的节点，发送提醒
 *
 * @author dinggang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceNodeTask {

    private final PerformanceNodeMapper nodeMapper;

    /**
     * 每天早上9点执行
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkExpiringNodes() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // 查询明天到期但还没完成的节点
        List<PerformanceNode> expiringNodes = nodeMapper.selectList(
                new LambdaQueryWrapper<PerformanceNode>()
                        .eq(PerformanceNode::getStatus, 1)  // 进行中
                        .le(PerformanceNode::getDueDate, tomorrow)
        );

        if (expiringNodes.isEmpty()) {
            log.info("履约节点提醒：无即将到期的节点");
            return;
        }

        log.info("履约节点提醒：发现 {} 个即将到期的节点", expiringNodes.size());
        for (PerformanceNode node : expiringNodes) {
            log.warn("节点[{}]即将到期，合同ID={}，截止日期={}",
                    node.getNodeName(), node.getContractId(), node.getDueDate());
            // TODO: 发送邮件/短信通知负责人
        }
    }
}

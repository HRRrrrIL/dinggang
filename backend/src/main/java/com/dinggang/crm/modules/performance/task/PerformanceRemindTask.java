package com.dinggang.crm.modules.performance.task;

import com.dinggang.crm.modules.performance.service.ContractNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 履约节点定时任务
 * 每日早上9点扫描到期节点并发送提醒
 *
 * @author dinggang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceRemindTask {

    private final ContractNodeService nodeService;

    /**
     * 每日早上9点执行：扫描即将到期和已逾期的节点
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void executeDailyRemind() {
        log.info("===== 履约节点每日提醒任务开始 =====");
        try {
            nodeService.scanAndRemind();
        } catch (Exception e) {
            log.error("履约节点提醒任务执行失败", e);
        }
        log.info("===== 履约节点每日提醒任务结束 =====");
    }
}

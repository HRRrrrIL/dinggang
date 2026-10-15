package com.dinggang.crm.modules.serviceorder.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dinggang.crm.common.exception.BusinessException;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.modules.serviceorder.entity.ServiceOrder;
import com.dinggang.crm.modules.serviceorder.mapper.ServiceOrderMapper;
import com.dinggang.crm.modules.serviceorder.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 售后工单服务实现
 *
 * @author dinggang
 */
@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {

    private final StringRedisTemplate redisTemplate;
    private static final String SEQ_KEY = "seq:order:";
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public PageResult<ServiceOrder> page(long page, long size, String orderNo, Integer type,
                                          Integer urgency, Integer status, Long assigneeId) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(orderNo != null && !orderNo.isEmpty(), ServiceOrder::getOrderNo, orderNo);
        wrapper.eq(type != null, ServiceOrder::getType, type);
        wrapper.eq(urgency != null, ServiceOrder::getUrgency, urgency);
        wrapper.eq(status != null, ServiceOrder::getStatus, status);
        wrapper.eq(assigneeId != null, ServiceOrder::getAssigneeId, assigneeId);
        wrapper.orderByDesc(ServiceOrder::getCreateTime);

        Page<ServiceOrder> result = this.page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public ServiceOrder createOrder(ServiceOrder order) {
        // 生成工单编号
        String month = LocalDateTime.now().format(MONTH_FMT);
        Long seq = redisTemplate.opsForValue().increment(SEQ_KEY + month);
        if (seq == null) seq = 1L;
        redisTemplate.expire(SEQ_KEY + month, 40, TimeUnit.DAYS);
        order.setOrderNo(String.format("WD-%s-%04d", month, seq));

        // 默认值
        order.setStatus(1);  // 待分配
        order.setSubmitterType(1);
        order.setSubmitterName(StpUtil.getLoginIdAsString());

        // SLA截止时间：按紧急度
        LocalDateTime now = LocalDateTime.now();
        if (order.getUrgency() == 3) {
            order.setSlaDeadline(now.plusHours(2));
        } else if (order.getUrgency() == 2) {
            order.setSlaDeadline(now.plusHours(4));
        } else {
            order.setSlaDeadline(now.plusHours(24));
        }

        this.save(order);
        return order;
    }

    @Override
    public void assignOrder(Long id, Long assigneeId) {
        ServiceOrder order = this.getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 1) throw new BusinessException("仅待分配状态可分配");

        ServiceOrder update = new ServiceOrder();
        update.setId(id);
        update.setAssigneeId(assigneeId);
        update.setStatus(2);  // 处理中
        update.setAssignTime(LocalDateTime.now());
        this.updateById(update);
    }

    @Override
    public void respondOrder(Long id, String response) {
        ServiceOrder order = this.getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 2) throw new BusinessException("仅处理中状态可响应");

        ServiceOrder update = new ServiceOrder();
        update.setId(id);
        update.setFirstResponseTime(LocalDateTime.now());
        this.updateById(update);
    }

    @Override
    public void finishOrder(Long id, String resolveDesc) {
        ServiceOrder order = this.getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 2) throw new BusinessException("仅处理中状态可完成");

        ServiceOrder update = new ServiceOrder();
        update.setId(id);
        update.setStatus(3);  // 待客户确认
        update.setFinishTime(LocalDateTime.now());
        update.setResolveDesc(resolveDesc);
        this.updateById(update);
    }

    @Override
    public void rejectOrder(Long id, String reason) {
        ServiceOrder order = this.getById(id);
        if (order == null) throw new BusinessException("工单不存在");

        ServiceOrder update = new ServiceOrder();
        update.setId(id);
        update.setStatus(5);  // 已驳回
        update.setRejectReason(reason);
        this.updateById(update);
    }
}

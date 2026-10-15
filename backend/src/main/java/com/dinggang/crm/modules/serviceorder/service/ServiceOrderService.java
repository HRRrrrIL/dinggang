package com.dinggang.crm.modules.serviceorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.modules.serviceorder.entity.ServiceOrder;

/**
 * 售后工单服务接口
 *
 * @author dinggang
 */
public interface ServiceOrderService extends IService<ServiceOrder> {

    /**
     * 分页查询工单
     */
    PageResult<ServiceOrder> page(long page, long size, String orderNo, Integer type,
                                   Integer urgency, Integer status, Long assigneeId);

    /**
     * 创建工单
     */
    ServiceOrder createOrder(ServiceOrder order);

    /**
     * 分配工单
     */
    void assignOrder(Long id, Long assigneeId);

    /**
     * 响应工单（首次响应）
     */
    void respondOrder(Long id, String response);

    /**
     * 完成工单
     */
    void finishOrder(Long id, String resolveDesc);

    /**
     * 驳回工单
     */
    void rejectOrder(Long id, String reason);
}

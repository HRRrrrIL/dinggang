package com.dinggang.crm.modules.serviceorder.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.serviceorder.entity.ServiceOrder;
import com.dinggang.crm.modules.serviceorder.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 售后工单控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService orderService;

    /**
     * 分页查询工单
     */
    @GetMapping
    @SaCheckPermission("order:view")
    public Result<PageResult<ServiceOrder>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer urgency,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long assigneeId) {
        return Result.success(orderService.page(page, size, orderNo, type, urgency, status, assigneeId));
    }

    /**
     * 创建工单
     */
    @PostMapping
    @SaCheckPermission("order:create")
    public Result<ServiceOrder> create(@RequestBody ServiceOrder order) {
        return Result.success(orderService.createOrder(order));
    }

    /**
     * 分配工单
     */
    @PostMapping("/{id}/assign")
    @SaCheckPermission("order:assign")
    public Result<Void> assign(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        orderService.assignOrder(id, body.get("assigneeId"));
        return Result.success();
    }

    /**
     * 响应工单
     */
    @PostMapping("/{id}/respond")
    @SaCheckPermission("order:handle")
    public Result<Void> respond(@PathVariable Long id, @RequestBody Map<String, String> body) {
        orderService.respondOrder(id, body.get("response"));
        return Result.success();
    }

    /**
     * 完成工单
     */
    @PostMapping("/{id}/finish")
    @SaCheckPermission("order:handle")
    public Result<Void> finish(@PathVariable Long id, @RequestBody Map<String, String> body) {
        orderService.finishOrder(id, body.get("resolveDesc"));
        return Result.success();
    }

    /**
     * 驳回工单
     */
    @PostMapping("/{id}/reject")
    @SaCheckPermission("order:handle")
    public Result<Void> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        orderService.rejectOrder(id, body.get("reason"));
        return Result.success();
    }
}

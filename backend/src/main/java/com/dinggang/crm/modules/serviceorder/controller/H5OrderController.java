package com.dinggang.crm.modules.serviceorder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dinggang.crm.common.exception.BusinessException;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.serviceorder.entity.SatisfactionScore;
import com.dinggang.crm.modules.serviceorder.entity.ServiceOrder;
import com.dinggang.crm.modules.serviceorder.mapper.SatisfactionScoreMapper;
import com.dinggang.crm.modules.serviceorder.mapper.ServiceOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * C端H5控制器（客户通过令牌访问，无需登录）
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/h5/order")
@RequiredArgsConstructor
public class H5OrderController {

    private final ServiceOrderMapper orderMapper;
    private final SatisfactionScoreMapper scoreMapper;

    /**
     * 通过工单编号查询工单详情
     */
    @GetMapping("/{orderNo}")
    public Result<Map<String, Object>> detail(@PathVariable String orderNo) {
        ServiceOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<ServiceOrder>().eq(ServiceOrder::getOrderNo, orderNo)
        );
        if (order == null) throw new BusinessException("工单不存在");

        // 隐藏敏感字段
        order.setAssigneeId(null);

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);

        // 查询是否已有评分
        SatisfactionScore score = scoreMapper.selectOne(
                new LambdaQueryWrapper<SatisfactionScore>().eq(SatisfactionScore::getOrderId, order.getId())
        );
        data.put("hasScored", score != null);
        data.put("score", score);

        return Result.success(data);
    }

    /**
     * 客户确认解决
     */
    @PostMapping("/{orderNo}/confirm")
    public Result<Void> confirm(@PathVariable String orderNo) {
        ServiceOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<ServiceOrder>().eq(ServiceOrder::getOrderNo, orderNo)
        );
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 3) throw new BusinessException("工单当前状态不可确认");

        ServiceOrder update = new ServiceOrder();
        update.setId(order.getId());
        update.setStatus(4);  // 已闭环
        orderMapper.updateById(update);
        return Result.success();
    }

    /**
     * 客户提交满意度评分
     */
    @PostMapping("/{orderNo}/score")
    public Result<SatisfactionScore> score(@PathVariable String orderNo, @RequestBody Map<String, Object> body) {
        ServiceOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<ServiceOrder>().eq(ServiceOrder::getOrderNo, orderNo)
        );
        if (order == null) throw new BusinessException("工单不存在");

        SatisfactionScore score = new SatisfactionScore();
        score.setOrderId(order.getId());
        score.setCustomerId(order.getCustomerId());
        score.setAssigneeId(order.getAssigneeId());
        score.setScore((Integer) body.get("score"));
        score.setSuggestion((String) body.get("suggestion"));
        score.setCreateTime(LocalDateTime.now());
        scoreMapper.insert(score);

        // 评分后自动闭环
        ServiceOrder update = new ServiceOrder();
        update.setId(order.getId());
        update.setStatus(4);
        orderMapper.updateById(update);

        return Result.success(score);
    }
}

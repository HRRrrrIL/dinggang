package com.dinggang.crm.modules.serviceorder.controller;

import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.serviceorder.entity.SatisfactionScore;
import com.dinggang.crm.modules.serviceorder.mapper.SatisfactionScoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 满意度评分控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/satisfaction")
@RequiredArgsConstructor
public class SatisfactionScoreController {

    private final SatisfactionScoreMapper scoreMapper;

    /**
     * 提交满意度评分
     */
    @PostMapping
    public Result<SatisfactionScore> submit(@RequestBody SatisfactionScore score) {
        scoreMapper.insert(score);
        return Result.success(score);
    }

    /**
     * 查询某工单的评分
     */
    @GetMapping("/order/{orderId}")
    public Result<SatisfactionScore> getByOrderId(@PathVariable Long orderId) {
        SatisfactionScore score = scoreMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SatisfactionScore>()
                        .eq(SatisfactionScore::getOrderId, orderId)
        );
        return Result.success(score);
    }
}

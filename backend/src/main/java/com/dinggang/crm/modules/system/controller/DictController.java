package com.dinggang.crm.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.system.entity.SysDictItem;
import com.dinggang.crm.modules.system.mapper.SysDictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典查询控制器
 * 前端下拉选项统一从这里获取
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/dict")
@RequiredArgsConstructor
public class DictController {

    private final SysDictItemMapper dictItemMapper;

    /**
     * 按类型获取字典项列表
     */
    @GetMapping("/{type}")
    public Result<List<SysDictItem>> getByType(@PathVariable String type) {
        List<SysDictItem> items = dictItemMapper.selectList(
                new LambdaQueryWrapper<SysDictItem>()
                        .eq(SysDictItem::getDictType, type)
                        .eq(SysDictItem::getStatus, 1)
                        .orderByAsc(SysDictItem::getSort)
        );
        return Result.success(items);
    }
}

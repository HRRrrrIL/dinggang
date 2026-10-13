package com.dinggang.crm.modules.contract.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.contract.entity.ContractTemplate;
import com.dinggang.crm.modules.contract.entity.ContractTemplateNode;
import com.dinggang.crm.modules.contract.service.ContractTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 合同模板管理控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/contract/templates")
@RequiredArgsConstructor
public class ContractTemplateController {

    private final ContractTemplateService templateService;

    /**
     * 分页查询模板列表
     */
    @GetMapping
    @SaCheckPermission("contract:template")
    public Result<PageResult<ContractTemplate>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.success(templateService.page(page, size, name, category, status));
    }

    /**
     * 获取模板详情（含节点清单）
     */
    @GetMapping("/{id}")
    @SaCheckPermission("contract:template")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(templateService.getTemplateDetail(id));
    }

    /**
     * 创建模板
     */
    @PostMapping
    @SaCheckPermission("contract:template")
    public Result<ContractTemplate> create(@RequestBody ContractTemplate template) {
        return Result.success(templateService.createTemplate(template));
    }

    /**
     * 更新模板
     */
    @PutMapping("/{id}")
    @SaCheckPermission("contract:template")
    public Result<ContractTemplate> update(@PathVariable Long id, @RequestBody ContractTemplate template) {
        return Result.success(templateService.updateTemplate(id, template));
    }

    /**
     * 删除模板（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("contract:template")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.removeById(id);
        return Result.success();
    }

    /**
     * 启用/禁用模板
     */
    @PutMapping("/{id}/status")
    @SaCheckPermission("contract:template")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        templateService.toggleStatus(id, status);
        return Result.success();
    }

    /**
     * 获取模板节点列表
     */
    @GetMapping("/{id}/nodes")
    @SaCheckPermission("contract:template")
    public Result<List<ContractTemplateNode>> nodes(@PathVariable Long id) {
        return Result.success(templateService.getTemplateNodes(id));
    }

    /**
     * 保存模板节点（全量覆盖）
     */
    @PutMapping("/{id}/nodes")
    @SaCheckPermission("contract:template")
    public Result<Void> saveNodes(@PathVariable Long id, @RequestBody List<ContractTemplateNode> nodes) {
        templateService.saveTemplateNodes(id, nodes);
        return Result.success();
    }
}

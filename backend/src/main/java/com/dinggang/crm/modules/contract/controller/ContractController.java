package com.dinggang.crm.modules.contract.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.contract.entity.Contract;
import com.dinggang.crm.modules.contract.service.ContractService;
import com.dinggang.crm.modules.performance.entity.PerformanceNode;
import com.dinggang.crm.modules.performance.mapper.PerformanceNodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 合同管理控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final PerformanceNodeMapper performanceNodeMapper;

    /**
     * 分页查询合同列表
     */
    @GetMapping
    @SaCheckPermission("contract:view")
    public Result<PageResult<Contract>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String contractName,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String contractType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long ownerId) {
        return Result.success(contractService.page(page, size, contractNo, contractName,
                customerId, contractType, status, ownerId));
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("contract:view")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(contractService.getContractDetail(id));
    }

    /**
     * 创建合同（草稿）
     */
    @PostMapping
    @SaCheckPermission("contract:create")
    public Result<Contract> create(@RequestBody Contract contract) {
        return Result.success(contractService.createContract(contract));
    }

    /**
     * 更新合同（仅草稿/已驳回）
     */
    @PutMapping("/{id}")
    @SaCheckPermission("contract:create")
    public Result<Contract> update(@PathVariable Long id, @RequestBody Contract contract) {
        return Result.success(contractService.updateContract(id, contract));
    }

    /**
     * 提交审批
     */
    @PostMapping("/{id}/submit")
    @SaCheckPermission("contract:create")
    public Result<Void> submit(@PathVariable Long id) {
        contractService.submitApproval(id);
        return Result.success();
    }

    /**
     * 审批合同
     */
    @PostMapping("/{id}/approve")
    @SaCheckPermission("contract:approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean pass = (Boolean) body.getOrDefault("pass", false);
        String comment = (String) body.get("comment");
        contractService.approveContract(id, pass, comment);
        return Result.success();
    }

    /**
     * 归档合同
     */
    @PostMapping("/{id}/archive")
    @SaCheckPermission("contract:approve")
    public Result<Void> archive(@PathVariable Long id) {
        contractService.archiveContract(id);
        return Result.success();
    }

    /**
     * 查询合同的履约节点列表
     */
    @GetMapping("/{id}/nodes")
    @SaCheckPermission("contract:view")
    public Result<List<PerformanceNode>> nodes(@PathVariable Long id) {
        List<PerformanceNode> nodes = performanceNodeMapper.selectList(
                new LambdaQueryWrapper<PerformanceNode>()
                        .eq(PerformanceNode::getContractId, id)
                        .orderByAsc(PerformanceNode::getDueDate)
        );
        return Result.success(nodes);
    }
}

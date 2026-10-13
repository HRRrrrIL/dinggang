package com.dinggang.crm.modules.performance.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.performance.entity.ContractNode;
import com.dinggang.crm.modules.performance.service.ContractNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 履约节点控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/contracts/{contractId}/nodes")
@RequiredArgsConstructor
public class ContractNodeController {

    private final ContractNodeService nodeService;

    /**
     * 查询合同的履约节点列表
     */
    @GetMapping
    @SaCheckPermission("performance:view")
    public Result<List<ContractNode>> list(@PathVariable Long contractId) {
        return Result.success(nodeService.listByContractId(contractId));
    }

    /**
     * 手动添加节点
     */
    @PostMapping
    @SaCheckPermission("performance:handle")
    public Result<ContractNode> add(@PathVariable Long contractId, @RequestBody ContractNode node) {
        node.setContractId(contractId);
        return Result.success(nodeService.addNode(node));
    }

    /**
     * 完成节点
     */
    @PostMapping("/{nodeId}/finish")
    @SaCheckPermission("performance:handle")
    public Result<Void> finish(@PathVariable Long contractId, @PathVariable Long nodeId) {
        nodeService.finishNode(nodeId);
        return Result.success();
    }
}

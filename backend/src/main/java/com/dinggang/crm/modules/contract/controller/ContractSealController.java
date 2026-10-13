package com.dinggang.crm.modules.contract.controller;

import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.contract.entity.ContractSeal;
import com.dinggang.crm.modules.contract.mapper.ContractSealMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 合同电子签章控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/contracts/{contractId}/seals")
@RequiredArgsConstructor
public class ContractSealController {

    private final ContractSealMapper sealMapper;

    /**
     * 查询合同的所有签章
     */
    @GetMapping
    public Result<List<ContractSeal>> list(@PathVariable Long contractId) {
        List<ContractSeal> seals = sealMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ContractSeal>()
                        .eq(ContractSeal::getContractId, contractId)
                        .orderByAsc(ContractSeal::getCreateTime)
        );
        return Result.success(seals);
    }

    /**
     * 添加签章
     */
    @PostMapping
    public Result<ContractSeal> add(@PathVariable Long contractId, @RequestBody ContractSeal seal) {
        seal.setContractId(contractId);
        seal.setStatus(1);  // 已盖章
        sealMapper.insert(seal);
        return Result.success(seal);
    }
}

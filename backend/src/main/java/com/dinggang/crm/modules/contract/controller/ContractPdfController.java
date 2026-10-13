package com.dinggang.crm.modules.contract.controller;

import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.contract.entity.Contract;
import com.dinggang.crm.modules.contract.entity.ContractSeal;
import com.dinggang.crm.modules.contract.mapper.ContractMapper;
import com.dinggang.crm.modules.contract.mapper.ContractSealMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同PDF/预览控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/contracts/{contractId}/pdf")
@RequiredArgsConstructor
public class ContractPdfController {

    private final ContractMapper contractMapper;
    private final ContractSealMapper sealMapper;

    /**
     * 生成合同预览HTML（可打印为PDF）
     */
    @GetMapping("/preview")
    public Result<Map<String, Object>> preview(@PathVariable Long contractId) {
        Contract contract = contractMapper.selectById(contractId);
        List<ContractSeal> seals = sealMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ContractSeal>()
                        .eq(ContractSeal::getContractId, contractId)
        );

        Map<String, Object> data = new HashMap<>();
        data.put("contract", contract);
        data.put("seals", seals);
        data.put("hasSeal", !seals.isEmpty());
        return Result.success(data);
    }
}

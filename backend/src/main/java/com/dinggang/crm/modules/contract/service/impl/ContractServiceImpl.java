package com.dinggang.crm.modules.contract.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dinggang.crm.common.exception.BusinessException;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.modules.contract.entity.Contract;
import com.dinggang.crm.modules.contract.mapper.ContractMapper;
import com.dinggang.crm.modules.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 合同服务实现
 *
 * @author dinggang
 */
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final StringRedisTemplate redisTemplate;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private static final String SEQ_KEY = "seq:contract:";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<Contract> page(long page, long size, String contractNo, String contractName,
                                      Long customerId, String contractType, Integer status, Long ownerId) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(contractNo != null && !contractNo.isEmpty(), Contract::getContractNo, contractNo);
        wrapper.like(contractName != null && !contractName.isEmpty(), Contract::getContractName, contractName);
        wrapper.eq(customerId != null, Contract::getCustomerId, customerId);
        wrapper.eq(contractType != null && !contractType.isEmpty(), Contract::getContractType, contractType);
        wrapper.eq(status != null, Contract::getStatus, status);
        wrapper.eq(ownerId != null, Contract::getOwnerId, ownerId);
        wrapper.orderByDesc(Contract::getCreateTime);

        Page<Contract> result = contractMapper.selectPage(new Page<>(page, size), wrapper);
        // 不返回金额密文
        result.getRecords().forEach(c -> c.setAmountCipher(null));
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public Map<String, Object> getContractDetail(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("contract", contract);
        // TODO: 联查审批记录、签章列表、履约节点列表
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Contract createContract(Contract contract) {
        // 生成合同编号
        String contractNo = generateContractNo();
        contract.setContractNo(contractNo);

        // 默认值
        contract.setStatus(1);  // 草稿
        contract.setVersion(1);
        contract.setPdfStatus(0);
        if (contract.getOwnerId() == null) {
            contract.setOwnerId(StpUtil.getLoginIdAsLong());
        }

        // 关键条款校验
        validateContract(contract);

        contractMapper.insert(contract);
        return contract;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Contract updateContract(Long id, Contract contract) {
        Contract existing = contractMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("合同不存在");
        }
        if (existing.getStatus() != 1 && existing.getStatus() != 4) {
            throw new BusinessException("仅草稿或已驳回状态可编辑");
        }

        contract.setId(id);
        contract.setContractNo(null);
        contract.setStatus(null);
        contract.setProcessInstanceId(null);
        contract.setVersion(existing.getVersion() + 1);

        validateContract(contract);
        contractMapper.updateById(contract);
        return contractMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (contract.getStatus() != 1 && contract.getStatus() != 4) {
            throw new BusinessException("仅草稿或已驳回状态可提交审批");
        }

        // 启动Flowable审批流程
        Map<String, Object> vars = new HashMap<>();
        vars.put("contractId", id);
        vars.put("applicant", StpUtil.getLoginIdAsString());

        ProcessInstance process = runtimeService.startProcessInstanceByKey(
                "contractApproval", "contract-" + id, vars);

        // 更新合同状态
        Contract update = new Contract();
        update.setId(id);
        update.setStatus(2);  // 审批中
        update.setProcessInstanceId(process.getId());
        contractMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveContract(Long id, boolean pass, String comment) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null || contract.getProcessInstanceId() == null) {
            throw new BusinessException("合同或流程不存在");
        }

        // 查询当前任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(contract.getProcessInstanceId())
                .list();

        if (tasks.isEmpty()) {
            throw new BusinessException("没有待处理的审批任务");
        }

        Task task = tasks.get(0);

        // 完成任务
        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", pass);
        taskService.complete(task.getId(), vars);

        // 更新合同状态
        Contract update = new Contract();
        update.setId(id);
        if (pass) {
            // 审批全部通过后状态变为已生效
            long remainingTasks = taskService.createTaskQuery()
                    .processInstanceId(contract.getProcessInstanceId())
                    .count();
            if (remainingTasks == 0) {
                update.setStatus(3);  // 已生效
            }
        } else {
            update.setStatus(4);  // 已驳回
            // 驳回时结束流程
            runtimeService.deleteProcessInstance(contract.getProcessInstanceId(), "驳回: " + comment);
        }
        contractMapper.updateById(update);
    }

    @Override
    public void archiveContract(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        Contract update = new Contract();
        update.setId(id);
        update.setStatus(5);  // 已归档
        update.setArchiveTime(LocalDateTime.now());
        update.setArchiveBy(StpUtil.getLoginIdAsLong());
        contractMapper.updateById(update);
    }

    /**
     * 关键条款校验
     */
    private void validateContract(Contract contract) {
        if (contract.getContractName() == null || contract.getContractName().isEmpty()) {
            throw new BusinessException("合同名称不能为空");
        }
        if (contract.getCustomerId() == null) {
            throw new BusinessException("客户不能为空");
        }
        // 结束日期必须晚于开始日期
        if (contract.getStartDate() != null && contract.getEndDate() != null) {
            if (contract.getEndDate().isBefore(contract.getStartDate())) {
                throw new BusinessException("到期日期不能早于生效日期");
            }
        }
    }

    /**
     * 生成合同编号: HT-YYYYMMDD-0001
     */
    private String generateContractNo() {
        String date = LocalDate.now().format(DATE_FMT);
        String key = SEQ_KEY + date;

        Long seq = redisTemplate.opsForValue().increment(key);
        if (seq == null) {
            seq = 1L;
        }
        redisTemplate.expire(key, 40, TimeUnit.DAYS);

        return String.format("HT-%s-%04d", date, seq);
    }
}

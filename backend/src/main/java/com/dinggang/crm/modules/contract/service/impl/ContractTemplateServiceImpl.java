package com.dinggang.crm.modules.contract.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dinggang.crm.common.exception.BusinessException;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.modules.contract.entity.ContractTemplate;
import com.dinggang.crm.modules.contract.entity.ContractTemplateNode;
import com.dinggang.crm.modules.contract.mapper.ContractTemplateMapper;
import com.dinggang.crm.modules.contract.mapper.ContractTemplateNodeMapper;
import com.dinggang.crm.modules.contract.service.ContractTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同模板服务实现
 *
 * @author dinggang
 */
@Service
@RequiredArgsConstructor
public class ContractTemplateServiceImpl extends ServiceImpl<ContractTemplateMapper, ContractTemplate> implements ContractTemplateService {

    private final ContractTemplateNodeMapper nodeMapper;

    @Override
    public PageResult<ContractTemplate> page(long page, long size, String name, String category, Integer status) {
        LambdaQueryWrapper<ContractTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), ContractTemplate::getTemplateName, name);
        wrapper.eq(category != null && !category.isEmpty(), ContractTemplate::getCategory, category);
        wrapper.eq(status != null, ContractTemplate::getStatus, status);
        wrapper.orderByDesc(ContractTemplate::getCreateTime);

        Page<ContractTemplate> result = this.page(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContractTemplate createTemplate(ContractTemplate template) {
        if (template.getVersion() == null) {
            template.setVersion("1.0");
        }
        if (template.getStatus() == null) {
            template.setStatus(1);
        }
        template.setUseCount(0);
        this.save(template);
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContractTemplate updateTemplate(Long id, ContractTemplate template) {
        ContractTemplate existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("模板不存在");
        }
        template.setId(id);
        template.setCreateBy(null);
        template.setCreateTime(null);
        this.updateById(template);
        return this.getById(id);
    }

    @Override
    public Map<String, Object> getTemplateDetail(Long id) {
        ContractTemplate template = this.getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("template", template);
        result.put("nodes", getTemplateNodes(id));
        return result;
    }

    @Override
    public List<ContractTemplateNode> getTemplateNodes(Long templateId) {
        return nodeMapper.selectList(
                new LambdaQueryWrapper<ContractTemplateNode>()
                        .eq(ContractTemplateNode::getTemplateId, templateId)
                        .eq(ContractTemplateNode::getStatus, 1)
                        .orderByAsc(ContractTemplateNode::getSort)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTemplateNodes(Long templateId, List<ContractTemplateNode> nodes) {
        // 先删除旧节点，再批量插入
        nodeMapper.delete(
                new LambdaQueryWrapper<ContractTemplateNode>()
                        .eq(ContractTemplateNode::getTemplateId, templateId)
        );

        if (nodes != null && !nodes.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                ContractTemplateNode node = nodes.get(i);
                node.setId(null);
                node.setTemplateId(templateId);
                if (node.getSort() == null) {
                    node.setSort(i + 1);
                }
                if (node.getStatus() == null) {
                    node.setStatus(1);
                }
                nodeMapper.insert(node);
            }
        }
    }

    @Override
    public void toggleStatus(Long id, Integer status) {
        ContractTemplate update = new ContractTemplate();
        update.setId(id);
        update.setStatus(status);
        this.updateById(update);
    }
}

package com.dinggang.crm.modules.contract.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.modules.contract.entity.ContractTemplate;
import com.dinggang.crm.modules.contract.entity.ContractTemplateNode;

import java.util.List;
import java.util.Map;

/**
 * 合同模板服务接口
 *
 * @author dinggang
 */
public interface ContractTemplateService extends IService<ContractTemplate> {

    /**
     * 分页查询模板列表
     */
    PageResult<ContractTemplate> page(long page, long size, String name, String category, Integer status);

    /**
     * 创建模板
     */
    ContractTemplate createTemplate(ContractTemplate template);

    /**
     * 更新模板
     */
    ContractTemplate updateTemplate(Long id, ContractTemplate template);

    /**
     * 获取模板详情（含节点清单）
     */
    Map<String, Object> getTemplateDetail(Long id);

    /**
     * 获取模板节点列表
     */
    List<ContractTemplateNode> getTemplateNodes(Long templateId);

    /**
     * 批量保存模板节点
     */
    void saveTemplateNodes(Long templateId, List<ContractTemplateNode> nodes);

    /**
     * 启用/禁用模板
     */
    void toggleStatus(Long id, Integer status);
}

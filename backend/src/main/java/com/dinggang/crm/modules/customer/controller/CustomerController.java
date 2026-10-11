package com.dinggang.crm.modules.customer.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.customer.entity.Customer;
import com.dinggang.crm.modules.customer.entity.CustomerContact;
import com.dinggang.crm.modules.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客户管理控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 分页查询客户列表
     */
    @GetMapping
    @SaCheckPermission("customer:view")
    public Result<PageResult<Customer>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) Long ownerId) {
        return Result.success(customerService.page(page, size, name, level, industry, ownerId));
    }

    /**
     * 获取客户详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("customer:view")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(customerService.getCustomerDetail(id));
    }

    /**
     * 创建客户
     */
    @PostMapping
    @SaCheckPermission("customer:create")
    public Result<Customer> create(@RequestBody Customer customer) {
        return Result.success(customerService.createCustomer(customer));
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    @SaCheckPermission("customer:edit")
    public Result<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        return Result.success(customerService.updateCustomer(id, customer));
    }

    /**
     * 删除客户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("customer:edit")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.removeById(id);
        return Result.success();
    }

    /**
     * 调整客户层级
     */
    @PutMapping("/{id}/level")
    @SaCheckPermission("customer:edit")
    public Result<Void> changeLevel(@PathVariable Long id, @RequestParam Integer level) {
        customerService.changeLevel(id, level);
        return Result.success();
    }

    // ========== 联系人管理 ==========

    /**
     * 获取客户联系人列表
     */
    @GetMapping("/{id}/contacts")
    @SaCheckPermission("customer:view")
    public Result<List<CustomerContact>> contacts(@PathVariable Long id) {
        return Result.success(customerService.getContacts(id));
    }

    /**
     * 添加联系人
     */
    @PostMapping("/{id}/contacts")
    @SaCheckPermission("customer:edit")
    public Result<CustomerContact> addContact(@PathVariable Long id, @RequestBody CustomerContact contact) {
        contact.setCustomerId(id);
        return Result.success(customerService.addContact(contact));
    }

    // ========== 标签管理 ==========

    /**
     * 获取客户标签
     */
    @GetMapping("/{id}/tags")
    @SaCheckPermission("customer:view")
    public Result<Object> tags(@PathVariable Long id) {
        return Result.success(customerService.getTags(id));
    }

    /**
     * 绑定客户标签（全量覆盖）
     */
    @PutMapping("/{id}/tags")
    @SaCheckPermission("customer:edit")
    public Result<Void> bindTags(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        customerService.bindTags(id, body.get("tagIds"));
        return Result.success();
    }
}

package com.dinggang.crm.modules.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.modules.customer.entity.Customer;
import com.dinggang.crm.modules.customer.entity.CustomerContact;
import com.dinggang.crm.modules.customer.entity.CustomerTag;

import java.util.List;
import java.util.Map;

/**
 * 客户服务接口
 *
 * @author dinggang
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 分页查询客户列表
     *
     * @param page      页码
     * @param size      每页大小
     * @param name      客户名称(模糊)
     * @param level     客户层级
     * @param industry  行业
     * @param ownerId   归属销售
     * @return 分页结果
     */
    PageResult<Customer> page(long page, long size, String name, Integer level,
                              String industry, Long ownerId);

    /**
     * 创建客户（自动生成客户编号）
     *
     * @param customer 客户信息
     * @return 新创建的客户
     */
    Customer createCustomer(Customer customer);

    /**
     * 更新客户
     *
     * @param id       客户ID
     * @param customer 更新信息
     * @return 更新后的客户
     */
    Customer updateCustomer(Long id, Customer customer);

    /**
     * 获取客户详情（含联系人+标签）
     *
     * @param id 客户ID
     * @return 客户详情
     */
    Map<String, Object> getCustomerDetail(Long id);

    /**
     * 获取客户联系人列表
     *
     * @param customerId 客户ID
     * @return 联系人列表
     */
    List<CustomerContact> getContacts(Long customerId);

    /**
     * 添加联系人
     *
     * @param contact 联系人信息
     * @return 保存后的联系人
     */
    CustomerContact addContact(CustomerContact contact);

    /**
     * 获取客户标签列表
     *
     * @param customerId 客户ID
     * @return 标签列表
     */
    List<CustomerTag> getTags(Long customerId);

    /**
     * 为客户绑定标签
     *
     * @param customerId 客户ID
     * @param tagIds     标签ID列表
     */
    void bindTags(Long customerId, List<Long> tagIds);

    /**
     * 调整客户层级（手动）
     *
     * @param id    客户ID
     * @param level 新层级
     */
    void changeLevel(Long id, Integer level);
}

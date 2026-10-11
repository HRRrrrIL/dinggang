package com.dinggang.crm.modules.customer.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dinggang.crm.common.exception.BusinessException;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.modules.customer.entity.*;
import com.dinggang.crm.modules.customer.mapper.CustomerContactMapper;
import com.dinggang.crm.modules.customer.mapper.CustomerMapper;
import com.dinggang.crm.modules.customer.mapper.CustomerTagMapper;
import com.dinggang.crm.modules.customer.mapper.CustomerTagRelMapper;
import com.dinggang.crm.modules.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 客户服务实现
 *
 * @author dinggang
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final CustomerContactMapper contactMapper;
    private final CustomerTagMapper tagMapper;
    private final CustomerTagRelMapper tagRelMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String SEQ_KEY = "seq:customer:";
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public PageResult<Customer> page(long page, long size, String name, Integer level,
                                       String industry, Long ownerId) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        // 条件筛选
        wrapper.like(name != null && !name.isEmpty(), Customer::getCustomerName, name);
        wrapper.eq(level != null, Customer::getLevel, level);
        wrapper.eq(industry != null && !industry.isEmpty(), Customer::getIndustry, industry);
        wrapper.eq(ownerId != null, Customer::getOwnerId, ownerId);

        // 按创建时间倒序
        wrapper.orderByDesc(Customer::getCreateTime);

        Page<Customer> pageResult = this.page(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer createCustomer(Customer customer) {
        // 1. 生成客户编号: C-YYYYMM-0001
        String customerNo = generateCustomerNo();
        customer.setCustomerNo(customerNo);

        // 2. 默认值
        if (customer.getLevel() == null) {
            customer.setLevel(1);  // 默认潜在
            customer.setLevelMode(1);
        }
        if (customer.getStatus() == null) {
            customer.setStatus(1);  // 默认跟进中
        }

        // 3. 归属销售 = 当前登录用户
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (customer.getOwnerId() == null) {
            customer.setOwnerId(currentUserId);
        }

        // 4. 保存
        this.save(customer);
        return customer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer updateCustomer(Long id, Customer customer) {
        Customer existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("客户不存在");
        }

        // 不允许修改编号和创建人
        customer.setId(id);
        customer.setCustomerNo(null);
        customer.setCreateBy(null);
        customer.setCreateTime(null);

        this.updateById(customer);
        return this.getById(id);
    }

    @Override
    public Map<String, Object> getCustomerDetail(Long id) {
        Customer customer = this.getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("customer", customer);

        // 联系人列表
        List<CustomerContact> contacts = getContacts(id);
        result.put("contacts", contacts);

        // 标签列表
        List<CustomerTag> tags = getTags(id);
        result.put("tags", tags);

        return result;
    }

    @Override
    public List<CustomerContact> getContacts(Long customerId) {
        return contactMapper.selectList(
                new LambdaQueryWrapper<CustomerContact>()
                        .eq(CustomerContact::getCustomerId, customerId)
                        .orderByDesc(CustomerContact::getIsPrimary)
        );
    }

    @Override
    public CustomerContact addContact(CustomerContact contact) {
        // TODO: 手机号加密存储（AES-GCM），同时生成脱敏值
        contactMapper.insert(contact);
        return contact;
    }

    @Override
    public List<CustomerTag> getTags(Long customerId) {
        // 联查：客户标签关联表 + 标签表
        List<CustomerTagRel> rels = tagRelMapper.selectList(
                new LambdaQueryWrapper<CustomerTagRel>()
                        .eq(CustomerTagRel::getCustomerId, customerId)
        );
        if (rels.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> tagIds = rels.stream().map(CustomerTagRel::getTagId).collect(Collectors.toList());
        return tagMapper.selectBatchIds(tagIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindTags(Long customerId, List<Long> tagIds) {
        // 先删除旧关联，再批量插入新关联
        tagRelMapper.delete(
                new LambdaQueryWrapper<CustomerTagRel>()
                        .eq(CustomerTagRel::getCustomerId, customerId)
        );

        if (tagIds != null && !tagIds.isEmpty()) {
            List<CustomerTagRel> rels = tagIds.stream().map(tagId -> {
                CustomerTagRel rel = new CustomerTagRel();
                rel.setCustomerId(customerId);
                rel.setTagId(tagId);
                return rel;
            }).collect(Collectors.toList());
            // 批量插入
            for (CustomerTagRel rel : rels) {
                tagRelMapper.insert(rel);
            }
        }
    }

    @Override
    public void changeLevel(Long id, Integer level) {
        Customer customer = this.getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // 校验层级范围
        if (level < 1 || level > 4) {
            throw new BusinessException("层级范围: 1-4");
        }

        Customer update = new Customer();
        update.setId(id);
        update.setLevel(level);
        update.setLevelMode(2);  // 手动调整
        this.updateById(update);
    }

    /**
     * 生成客户编号: C-YYYYMM-0001
     * 使用Redis INCR保证唯一性，DB唯一索引兜底
     */
    private String generateCustomerNo() {
        String month = LocalDateTime.now().format(MONTH_FMT);
        String key = SEQ_KEY + month;

        Long seq = redisTemplate.opsForValue().increment(key);
        if (seq == null) {
            seq = 1L;
        }
        // 月份key过期时间：40天后自动清理
        redisTemplate.expire(key, 40, TimeUnit.DAYS);

        return String.format("C-%s-%04d", month, seq);
    }
}

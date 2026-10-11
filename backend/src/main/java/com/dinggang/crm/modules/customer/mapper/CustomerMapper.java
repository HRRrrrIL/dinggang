package com.dinggang.crm.modules.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dinggang.crm.modules.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户Mapper
 *
 * @author dinggang
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}

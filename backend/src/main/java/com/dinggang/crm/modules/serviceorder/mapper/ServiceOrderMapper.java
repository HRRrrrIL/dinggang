package com.dinggang.crm.modules.serviceorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dinggang.crm.modules.serviceorder.entity.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 售后工单Mapper
 *
 * @author dinggang
 */
@Mapper
public interface ServiceOrderMapper extends BaseMapper<ServiceOrder> {
}

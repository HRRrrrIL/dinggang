package com.dinggang.crm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dinggang.crm.modules.system.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 *
 * @author dinggang
 */
@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {
}

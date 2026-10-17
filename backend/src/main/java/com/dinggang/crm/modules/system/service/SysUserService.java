package com.dinggang.crm.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dinggang.crm.modules.system.entity.SysUser;

/**
 * 系统用户服务接口
 *
 * @author dinggang
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 登录验证
     *
     * @param username 账号
     * @param password 明文密码
     * @return 用户信息
     */
    SysUser login(String username, String password);
}

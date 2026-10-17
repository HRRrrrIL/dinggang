package com.dinggang.crm.modules.system.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dinggang.crm.common.exception.BusinessException;
import com.dinggang.crm.modules.system.entity.SysUser;
import com.dinggang.crm.modules.system.mapper.SysUserMapper;
import com.dinggang.crm.modules.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统用户服务实现
 *
 * @author dinggang
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser login(String username, String password) {
        // 按账号查询
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        if (user == null) {
            throw new BusinessException("账号不存在");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已禁用，请联系管理员");
        }

        // BCrypt密码校验
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 清除密码后返回
        user.setPassword(null);
        return user;
    }
}

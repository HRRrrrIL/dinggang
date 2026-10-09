package com.dinggang.crm.common.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dinggang.crm.modules.system.entity.SysUser;
import com.dinggang.crm.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时自动修正admin密码（临时工具，上线前删掉）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordFixRunner implements CommandLineRunner {

    private final SysUserMapper userMapper;

    @Override
    public void run(String... args) {
        String newHash = BCrypt.hashpw("admin123");
        userMapper.update(null,
                new LambdaUpdateWrapper<SysUser>()
                        .eq(SysUser::getUsername, "admin")
                        .set(SysUser::getPassword, newHash)
        );
        log.info("===== admin密码已重置为 admin123 =====");
    }
}

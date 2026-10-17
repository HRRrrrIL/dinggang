package com.dinggang.crm.modules.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.system.entity.SysUser;
import com.dinggang.crm.modules.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 - 登录/登出/当前用户
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // 调用Service验证用户
        SysUser user = sysUserService.login(username, password);

        // Sa-Token登录
        StpUtil.login(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("tokenName", StpUtil.getTokenName());
        result.put("tokenValue", StpUtil.getTokenValue());
        result.put("userInfo", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "realName", user.getRealName()
        ));

        return Result.success(result);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserService.getById(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        // TODO: 查询用户角色和权限列表

        return Result.success(result);
    }
}

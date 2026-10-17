package com.dinggang.crm.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dinggang.crm.common.result.PageResult;
import com.dinggang.crm.common.result.Result;
import com.dinggang.crm.modules.system.entity.SysUser;
import com.dinggang.crm.modules.system.entity.SysUserRole;
import com.dinggang.crm.modules.system.mapper.SysUserMapper;
import com.dinggang.crm.modules.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 *
 * @author dinggang
 */
@RestController
@RequestMapping("/system/users")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @SaCheckPermission("system:user")
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String username) {

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null && !username.isEmpty(), SysUser::getUsername, username);
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = userMapper.selectPage(new Page<>(page, size), wrapper);
        // 不返回密码
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), page, size));
    }

    /**
     * 新增用户
     */
    @PostMapping
    @SaCheckPermission("system:user")
    public Result<SysUser> create(@RequestBody Map<String, Object> body) {
        SysUser user = new SysUser();
        user.setUsername((String) body.get("username"));
        user.setRealName((String) body.get("realName"));
        user.setEmail((String) body.get("email"));
        user.setStatus(1);
        // 默认密码 123456
        user.setPassword(BCrypt.hashpw("123456"));
        userMapper.insert(user);
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 分配角色
     */
    @PostMapping("/{id}/roles")
    @SaCheckPermission("system:role")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long roleId = body.get("roleId");
        // 先删除旧关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        // 新增关联
        SysUserRole ur = new SysUserRole();
        ur.setUserId(id);
        ur.setRoleId(roleId);
        userRoleMapper.insert(ur);
        return Result.success();
    }
}

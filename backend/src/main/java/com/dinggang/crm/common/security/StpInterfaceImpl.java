package com.dinggang.crm.common.security;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dinggang.crm.modules.system.entity.SysPermission;
import com.dinggang.crm.modules.system.entity.SysRole;
import com.dinggang.crm.modules.system.entity.SysUserRole;
import com.dinggang.crm.modules.system.mapper.SysPermissionMapper;
import com.dinggang.crm.modules.system.mapper.SysRoleMapper;
import com.dinggang.crm.modules.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限接口实现
 * 提供用户权限码和角色码给Sa-Token做校验
 *
 * @author dinggang
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;

    /**
     * 返回用户拥有的权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());

        // 查用户的所有角色
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 查这些角色对应的所有权限
        // TODO: 联查 sys_role_permission 和 sys_permission
        // 简化版：直接返回所有权限（开发阶段，上线前改成按角色查）
        List<SysPermission> permissions = permissionMapper.selectList(null);
        return permissions.stream()
                .map(SysPermission::getPermCode)
                .collect(Collectors.toList());
    }

    /**
     * 返回用户拥有的角色码列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());

        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        return roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }
}

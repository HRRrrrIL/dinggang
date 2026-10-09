package com.dinggang.crm.common.security;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;

/**
 * 数据权限处理器
 * 销售角色只能看自己创建的数据，管理员/创始人看全部
 *
 * @author dinggang
 */
public class CustomDataPermissionHandler implements DataPermissionHandler {

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        try {
            // 系统表/权限相关查询直接放行，防止递归调用
            if (mappedStatementId == null) {
                return where;
            }
            // 系统权限、用户、角色相关的Mapper不做数据权限处理
            if (mappedStatementId.contains(".system.")
                    || mappedStatementId.contains("SysUser")
                    || mappedStatementId.contains("SysRole")
                    || mappedStatementId.contains("SysPermission")
                    || mappedStatementId.contains("SysUserRole")) {
                return where;
            }

            // 未登录不处理
            if (!StpUtil.isLogin()) {
                return where;
            }

            // 管理员/创始人看全部数据
            if (StpUtil.hasRole("ADMIN") || StpUtil.hasRole("FOUNDER")) {
                return where;
            }

            Long userId = StpUtil.getLoginIdAsLong();

            // 销售角色：只能看自己的数据
            if (StpUtil.hasRole("SALE")) {
                EqualsTo ownerEq = new EqualsTo(new Column("owner_id"), new LongValue(userId));
                if (where == null) {
                    return ownerEq;
                }
                return new AndExpression(where, ownerEq);
            }

            // 其他角色默认看全部（OPS/LEGAL）
            return where;

        } catch (Exception e) {
            return where;
        }
    }
}

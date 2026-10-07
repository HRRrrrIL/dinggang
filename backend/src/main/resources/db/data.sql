-- ============================================================
-- dinggang CRM 初始化数据脚本
-- 版本: v1.1
-- 说明: 部门/角色/权限/字典/初始管理员
-- ============================================================

-- ============================================================
-- 1. 部门初始化
-- ============================================================
INSERT INTO sys_dept (id, dept_name, dept_code, parent_id, sort, status) VALUES
(1, '总经办',     'EXEC', 0, 1, 1),
(2, '销售部',     'SALE', 0, 2, 1),
(3, '法务部',     'LEGAL', 0, 3, 1),
(4, '运营部',     'OPS',  0, 4, 1),
(5, '技术组',     'TECH', 4, 1, 1);

-- ============================================================
-- 2. 角色初始化
-- ============================================================
INSERT INTO sys_role (id, role_code, role_name, data_scope, remark) VALUES
(1, 'ADMIN',    '系统管理员', 1, '系统最高权限'),
(2, 'FOUNDER', '创始人',     1, '查看所有数据、导出报表、调整权限'),
(3, 'SALE',    '销售岗',     3, '客户CRUD、创建合同、创建工单'),
(4, 'LEGAL',   '法务岗',     2, '合同审核、模板库维护'),
(5, 'OPS',     '运营岗',     2, '处理工单、跟踪履约、查看满意度');

-- ============================================================
-- 3. 权限点初始化
-- ============================================================
INSERT INTO sys_permission (id, perm_code, perm_name, module, type, parent_id, sort) VALUES
-- 客户模块
(100, 'customer:view',   '查看客户',   'customer', 3, 0, 1),
(101, 'customer:edit',   '编辑客户',   'customer', 3, 0, 2),
(102, 'customer:create', '创建客户',   'customer', 3, 0, 3),
-- 合同模块
(200, 'contract:view',     '查看合同',     'contract', 3, 0, 1),
(201, 'contract:create',   '创建合同',     'contract', 3, 0, 2),
(202, 'contract:approve',  '审批合同',     'contract', 3, 0, 3),
(203, 'contract:template',  '管理合同模板', 'contract', 3, 0, 4),
(204, 'contract:export',   '导出合同',     'contract', 3, 0, 5),
-- 履约模块
(300, 'performance:view',   '查看履约',   'performance', 3, 0, 1),
(301, 'performance:handle', '处理履约',   'performance', 3, 0, 2),
-- 工单模块
(400, 'order:view',     '查看工单',   'order', 3, 0, 1),
(401, 'order:create',    '创建工单',   'order', 3, 0, 2),
(402, 'order:assign',    '分配工单',   'order', 3, 0, 3),
(403, 'order:handle',   '处理工单',   'order', 3, 0, 4),
-- 统计模块
(500, 'stats:view',     '查看统计',   'stats', 3, 0, 1),
(501, 'stats:export',  '导出报表',   'stats', 3, 0, 2),
-- 系统模块
(600, 'system:user',     '用户管理',   'system', 3, 0, 1),
(601, 'system:role',     '角色权限管理', 'system', 3, 0, 2),
(602, 'system:log',      '操作日志',   'system', 3, 0, 3),
(603, 'system:dict',      '字典管理',   'system', 3, 0, 4),
-- AI模块
(700, 'ai:scan',    '合同AI扫描', 'ai', 3, 0, 1),
(701, 'ai:knowledge', '知识库管理', 'ai', 3, 0, 2);

-- ============================================================
-- 4. 角色权限关联
-- ============================================================
-- ADMIN: 全部权限
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT 1, id FROM sys_permission;

-- FOUNDER: 全部查看+导出
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
(2, 100), (2, 200), (2, 300), (2, 400), (2, 500), (2, 501), (2, 602);

-- SALE: 客户+合同创建+工单创建
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
(3, 100), (3, 101), (3, 102),
(3, 200), (3, 201),
(3, 300),
(3, 400), (3, 401),
(3, 700);

-- LEGAL: 合同查看+审批+模板管理
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
(4, 200), (4, 202), (4, 203), (4, 204),
(4, 700), (4, 701);

-- OPS: 履约+工单处理
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
(5, 300), (5, 301),
(5, 400), (5, 402), (5, 403),
(5, 701);

-- ============================================================
-- 5. 初始管理员账号
-- 密码: admin123 (BCrypt加密)
-- ============================================================
INSERT INTO sys_user (id, username, password, real_name, dept_id, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- ============================================================
-- 6. 字典初始化
-- ============================================================

-- 客户层级
INSERT INTO sys_dict (dict_type, dict_name) VALUES
('CUSTOMER_LEVEL', '客户层级'),
('CONTRACT_TYPE', '合同类型'),
('CONTRACT_STATUS', '合同状态'),
('ORDER_TYPE', '工单类型'),
('ORDER_URGENCY', '工单紧急度'),
('ORDER_STATUS', '工单状态'),
('NODE_TYPE', '履约节点类型'),
('NODE_STATUS', '履约节点状态'),
('TAG_TYPE', '标签类型'),
('USER_STATUS', '用户状态');

INSERT INTO sys_dict_item (dict_type, item_value, item_label, sort) VALUES
-- 客户层级
('CUSTOMER_LEVEL', '1', '潜在客户', 1),
('CUSTOMER_LEVEL', '2', '意向客户', 2),
('CUSTOMER_LEVEL', '3', '成交客户', 3),
('CUSTOMER_LEVEL', '4', '复购客户', 4),
-- 合同类型
('CONTRACT_TYPE', 'SALE', '产品销售合同', 1),
('CONTRACT_TYPE', 'PURCHASE', '采购合同', 2),
('CONTRACT_TYPE', 'SERVICE', '服务合作合同', 3),
('CONTRACT_TYPE', 'FRAME', '框架协议', 4),
('CONTRACT_TYPE', 'OTHER', '其他', 5),
-- 合同状态
('CONTRACT_STATUS', '1', '草稿', 1),
('CONTRACT_STATUS', '2', '审批中', 2),
('CONTRACT_STATUS', '3', '已生效', 3),
('CONTRACT_STATUS', '4', '已驳回', 4),
('CONTRACT_STATUS', '5', '已归档', 5),
-- 工单类型
('ORDER_TYPE', '1', '合同履约问题', 1),
('ORDER_TYPE', '2', '产品故障', 2),
('ORDER_TYPE', '3', '服务咨询', 3),
('ORDER_TYPE', '4', '投诉建议', 4),
('ORDER_TYPE', '5', '其他', 5),
-- 工单紧急度
('ORDER_URGENCY', '1', '普通(24h响应)', 1),
('ORDER_URGENCY', '2', '紧急(4h响应)', 2),
('ORDER_URGENCY', '3', '特急(2h响应)', 3),
-- 工单状态
('ORDER_STATUS', '1', '待分配', 1),
('ORDER_STATUS', '2', '处理中', 2),
('ORDER_STATUS', '3', '待客户确认', 3),
('ORDER_STATUS', '4', '已闭环', 4),
('ORDER_STATUS', '5', '已驳回', 5),
-- 节点类型
('NODE_TYPE', '1', '付款节点', 1),
('NODE_TYPE', '2', '交付节点', 2),
('NODE_TYPE', '3', '其他节点', 3),
-- 节点状态
('NODE_STATUS', '0', '待执行', 1),
('NODE_STATUS', '1', '进行中', 2),
('NODE_STATUS', '2', '已完成', 3),
('NODE_STATUS', '3', '已逾期', 4),
-- 标签类型
('TAG_TYPE', 'INDUSTRY', '行业类型', 1),
('TAG_TYPE', 'DEMAND', '需求方向', 2),
('TAG_TYPE', 'INTENT', '合作意向度', 3),
('TAG_TYPE', 'COOP', '合作偏好', 4),
-- 用户状态
('USER_STATUS', '0', '禁用', 1),
('USER_STATUS', '1', '正常', 2);

-- ============================================================
-- 初始化完成
-- ============================================================

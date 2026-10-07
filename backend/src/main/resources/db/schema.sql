-- ============================================================
-- dinggang CRM 数据库建表脚本
-- 版本: v1.1
-- 日期: 2026-10-21
-- 说明: 30张表，含索引与约束；无物理外键，应用层保证一致性
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 一、系统与权限模块（10张）
-- ============================================================

-- 1. 部门表
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    dept_name   VARCHAR(50)  NOT NULL COMMENT '部门名称',
    dept_code   VARCHAR(30)  NOT NULL COMMENT '部门编码: SALE/LEGAL/OPS/TECH/FIN',
    leader_id   BIGINT       DEFAULT NULL COMMENT '负责人ID',
    parent_id   BIGINT       DEFAULT 0 COMMENT '父部门ID',
    sort        INT          DEFAULT 0 COMMENT '排序',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT      DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_code (dept_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 2. 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username      VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password      VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    real_name     VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    phone_cipher  VARCHAR(255) DEFAULT NULL COMMENT '手机号密文(AES-GCM)',
    phone_mask    VARCHAR(20)  DEFAULT NULL COMMENT '手机号脱敏展示',
    email         VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    dept_id       BIGINT       DEFAULT NULL COMMENT '所属部门ID',
    manager_id    BIGINT       DEFAULT NULL COMMENT '直属上级ID',
    status        TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag      TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_dept_id (dept_id),
    KEY idx_manager_id (manager_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_code   VARCHAR(30)  NOT NULL COMMENT '角色编码: SALE/LEGAL/OPS/FOUNDER/ADMIN',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    data_scope  TINYINT      DEFAULT 3 COMMENT '数据范围: 1全部 2本部门 3本人',
    remark      VARCHAR(200) DEFAULT NULL COMMENT '备注',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 4. 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT  NOT NULL COMMENT '用户ID',
    role_id     BIGINT  NOT NULL COMMENT '角色ID',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 5. 权限点表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    perm_code   VARCHAR(100) NOT NULL COMMENT '权限编码: 如 contract:approve',
    perm_name   VARCHAR(50)  NOT NULL COMMENT '权限名称',
    module      VARCHAR(30)  NOT NULL COMMENT '所属模块',
    type        TINYINT      NOT NULL COMMENT '类型: 1菜单 2按钮 3接口',
    parent_id   BIGINT       DEFAULT 0 COMMENT '父权限ID',
    sort        INT          DEFAULT 0 COMMENT '排序',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限点表';

-- 6. 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_id     BIGINT  NOT NULL COMMENT '角色ID',
    perm_id     BIGINT  NOT NULL COMMENT '权限ID',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, perm_id),
    KEY idx_perm_id (perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 7. 操作日志表
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id     BIGINT        DEFAULT NULL COMMENT '操作人ID',
    username    VARCHAR(50)   DEFAULT NULL COMMENT '操作人账号',
    module      VARCHAR(30)   DEFAULT NULL COMMENT '模块',
    action      VARCHAR(50)   DEFAULT NULL COMMENT '操作类型',
    biz_type    VARCHAR(30)   DEFAULT NULL COMMENT '业务类型',
    biz_id      VARCHAR(64)   DEFAULT NULL COMMENT '业务ID',
    detail      VARCHAR(500)  DEFAULT NULL COMMENT '操作详情',
    params      VARCHAR(2000) DEFAULT NULL COMMENT '请求参数(脱敏截断)',
    ip          VARCHAR(50)   DEFAULT NULL COMMENT 'IP地址',
    ua          VARCHAR(200)  DEFAULT NULL COMMENT 'User-Agent',
    result      TINYINT       DEFAULT 1 COMMENT '结果: 1成功 0失败',
    cost_ms     BIGINT        DEFAULT NULL COMMENT '耗时(毫秒)',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_user_time (user_id, create_time),
    KEY idx_module_time (module, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 8. 字典表
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    dict_type   VARCHAR(50)  NOT NULL COMMENT '字典类型',
    dict_name   VARCHAR(100) NOT NULL COMMENT '字典名称',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典表';

-- 9. 字典项表
DROP TABLE IF EXISTS sys_dict_item;
CREATE TABLE sys_dict_item (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典项ID',
    dict_type   VARCHAR(50)  NOT NULL COMMENT '字典类型',
    item_value  VARCHAR(50)  NOT NULL COMMENT '字典值',
    item_label  VARCHAR(100) NOT NULL COMMENT '字典标签',
    sort        INT          DEFAULT 0 COMMENT '排序',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_value (dict_type, item_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项表';

-- 10. 站内消息表
DROP TABLE IF EXISTS sys_message;
CREATE TABLE sys_message (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    user_id     BIGINT       NOT NULL COMMENT '接收人ID',
    type        TINYINT      NOT NULL COMMENT '类型: 1待办 2提醒 3系统',
    title       VARCHAR(200) NOT NULL COMMENT '标题',
    content     VARCHAR(1000) DEFAULT NULL COMMENT '内容',
    biz_type    VARCHAR(30)  DEFAULT NULL COMMENT '业务类型',
    biz_id      VARCHAR(64)  DEFAULT NULL COMMENT '业务ID',
    link        VARCHAR(200) DEFAULT NULL COMMENT '跳转链接',
    read_flag   TINYINT      DEFAULT 0 COMMENT '已读: 0未读 1已读',
    read_time    DATETIME     DEFAULT NULL COMMENT '阅读时间',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_read (user_id, read_flag),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息表';

-- ============================================================
-- 二、客户模块（4张）
-- ============================================================

-- 11. 客户表
DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '客户ID',
    customer_no     VARCHAR(30)  NOT NULL COMMENT '客户编号: C-YYYYMM-0001',
    customer_name  VARCHAR(100) NOT NULL COMMENT '客户名称',
    level           TINYINT      DEFAULT 1 COMMENT '层级: 1潜在 2意向 3成交 4复购',
    level_mode      TINYINT      DEFAULT 1 COMMENT '层级模式: 1自动 2手动',
    industry        VARCHAR(50)  DEFAULT NULL COMMENT '行业(冗余自标签)',
    source          VARCHAR(50)  DEFAULT NULL COMMENT '客户来源',
    demand_desc      TEXT         COMMENT '需求描述',
    preference      TEXT         COMMENT '客户偏好',
    intent_degree   TINYINT      DEFAULT 3 COMMENT '意向度: 1-5',
    owner_id        BIGINT       DEFAULT NULL COMMENT '归属销售ID',
    status          TINYINT      DEFAULT 1 COMMENT '状态: 0流失 1跟进中',
    last_follow_time DATETIME    DEFAULT NULL COMMENT '最近跟进时间',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新人',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_no (customer_no),
    KEY idx_owner_id (owner_id),
    KEY idx_level (level),
    KEY idx_industry (industry),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 12. 标签表
DROP TABLE IF EXISTS customer_tag;
CREATE TABLE customer_tag (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    tag_name    VARCHAR(30)  NOT NULL COMMENT '标签名',
    tag_type    VARCHAR(30)  NOT NULL COMMENT '类型: INDUSTRY/DEMAND/INTENT/COOP',
    color       VARCHAR(10)  DEFAULT NULL COMMENT '标签颜色',
    sort        INT          DEFAULT 0 COMMENT '排序',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name_type (tag_name, tag_type),
    KEY idx_tag_type (tag_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签表';

-- 13. 客户标签关联表
DROP TABLE IF EXISTS customer_tag_rel;
CREATE TABLE customer_tag_rel (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_id BIGINT   NOT NULL COMMENT '客户ID',
    tag_id      BIGINT   NOT NULL COMMENT '标签ID',
    create_by   BIGINT   DEFAULT NULL COMMENT '创建人',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_tag (customer_id, tag_id),
    KEY idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签关联表';

-- 14. 联系人表
DROP TABLE IF EXISTS customer_contact;
CREATE TABLE customer_contact (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '联系人ID',
    customer_id     BIGINT       NOT NULL COMMENT '客户ID',
    name            VARCHAR(50)  NOT NULL COMMENT '姓名',
    position        VARCHAR(50)  DEFAULT NULL COMMENT '职位',
    phone_cipher    VARCHAR(255) DEFAULT NULL COMMENT '手机号密文',
    phone_mask      VARCHAR(20)  DEFAULT NULL COMMENT '手机号脱敏',
    email           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    is_primary      TINYINT      DEFAULT 0 COMMENT '是否主要联系人: 0否 1是',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户联系人表';

-- ============================================================
-- 三、合同模块（6张）
-- ============================================================

-- 15. 合同模板表
DROP TABLE IF EXISTS contract_template;
CREATE TABLE contract_template (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    template_name   VARCHAR(100) NOT NULL COMMENT '模板名称',
    category        VARCHAR(30)  NOT NULL COMMENT '分类: SALE/PURCHASE/SERVICE/FRAME',
    scene_desc      VARCHAR(200) DEFAULT NULL COMMENT '适用场景',
    content_json    LONGTEXT     COMMENT '条款结构JSON',
    content_html    LONGTEXT     COMMENT 'HTML内容',
    status          TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    version         VARCHAR(20)  DEFAULT '1.0' COMMENT '版本号',
    use_count       INT          DEFAULT 0 COMMENT '使用次数',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同模板表';

-- 16. 模板节点清单表
DROP TABLE IF EXISTS contract_template_node;
CREATE TABLE contract_template_node (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '节点ID',
    template_id     BIGINT       NOT NULL COMMENT '模板ID',
    node_name       VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_type       TINYINT      DEFAULT 3 COMMENT '类型: 1付款 2交付 3其他',
    offset_days     INT          DEFAULT 0 COMMENT '相对生效日偏移天数',
    default_owner_role VARCHAR(30) DEFAULT NULL COMMENT '默认责任角色',
    finish_standard VARCHAR(500) DEFAULT NULL COMMENT '完成标准',
    sort            INT          DEFAULT 0 COMMENT '排序',
    status          TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    PRIMARY KEY (id),
    KEY idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板节点清单表';

-- 17. 合同表
DROP TABLE IF EXISTS contract;
CREATE TABLE contract (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '合同ID',
    contract_no         VARCHAR(30)   NOT NULL COMMENT '合同编号: HT-YYYYMMDD-0001',
    contract_name       VARCHAR(200)  NOT NULL COMMENT '合同名称',
    customer_id         BIGINT        NOT NULL COMMENT '客户ID',
    template_id         BIGINT        DEFAULT NULL COMMENT '模板ID',
    contract_type       VARCHAR(30)   DEFAULT 'OTHER' COMMENT '合同类型: SALE/PURCHASE/SERVICE/FRAME/OTHER',
    amount_cipher       VARBINARY(255) DEFAULT NULL COMMENT '金额密文(AES-GCM)',
    amount_flag         TINYINT       DEFAULT 0 COMMENT '是否有金额: 0无 1有',
    currency            VARCHAR(10)   DEFAULT 'CNY' COMMENT '币种',
    sign_date           DATE          DEFAULT NULL COMMENT '签订日期',
    start_date          DATE          DEFAULT NULL COMMENT '生效日期',
    end_date            DATE          DEFAULT NULL COMMENT '到期日期',
    content_json        LONGTEXT      COMMENT '条款结构JSON',
    content_html        LONGTEXT      COMMENT 'HTML渲染缓存',
    status              TINYINT       DEFAULT 1 COMMENT '状态: 1草稿 2审批中 3已生效 4已驳回 5已归档',
    owner_id            BIGINT        DEFAULT NULL COMMENT '归属销售ID',
    process_instance_id VARCHAR(64)   DEFAULT NULL COMMENT 'Flowable流程实例ID',
    pdf_object_key      VARCHAR(255) DEFAULT NULL COMMENT '成品PDF对象键',
    pdf_status           TINYINT       DEFAULT 0 COMMENT 'PDF状态: 0未生成 1生成中 2成功 3失败',
    archive_time        DATETIME      DEFAULT NULL COMMENT '归档时间',
    archive_by         BIGINT        DEFAULT NULL COMMENT '归档人',
    version             INT           DEFAULT 1 COMMENT '内容版本号',
    create_by           BIGINT        DEFAULT NULL COMMENT '创建人',
    create_time          DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           BIGINT        DEFAULT NULL COMMENT '更新人',
    update_time          DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag            TINYINT       DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contract_no (contract_no),
    KEY idx_customer_id (customer_id),
    KEY idx_owner_id (owner_id),
    KEY idx_contract_type (contract_type),
    KEY idx_status (status),
    KEY idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- 18. 合同审批记录表
DROP TABLE IF EXISTS contract_approval;
CREATE TABLE contract_approval (
    id                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '审批ID',
    contract_id         BIGINT       NOT NULL COMMENT '合同ID',
    process_instance_id VARCHAR(64)  DEFAULT NULL COMMENT '流程实例ID',
    task_id             VARCHAR(64)  DEFAULT NULL COMMENT 'Flowable任务ID',
    node_key            VARCHAR(50)  DEFAULT NULL COMMENT '节点Key',
    node_name           VARCHAR(50)  DEFAULT NULL COMMENT '节点名称',
    approver_id         BIGINT       DEFAULT NULL COMMENT '审批人ID',
    approver_name       VARCHAR(50)  DEFAULT NULL COMMENT '审批人姓名',
    result              TINYINT      DEFAULT NULL COMMENT '结果: 1通过 2驳回',
    comment             VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    attachment          JSON         DEFAULT NULL COMMENT '审批附件',
    action_time         DATETIME     DEFAULT NULL COMMENT '操作时间',
    duration_ms         BIGINT       DEFAULT NULL COMMENT '节点耗时(毫秒)',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id),
    KEY idx_process_instance (process_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同审批记录表';

-- 19. 合同签章表
DROP TABLE IF EXISTS contract_seal;
CREATE TABLE contract_seal (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '签章ID',
    contract_id     BIGINT       NOT NULL COMMENT '合同ID',
    seal_type       TINYINT      NOT NULL COMMENT '类型: 1企业 2个人',
    seal_object_key VARCHAR(255) NOT NULL COMMENT '签章图片对象键',
    signer_name     VARCHAR(50)  DEFAULT NULL COMMENT '签署人姓名',
    signer_id_mask  VARCHAR(50)  DEFAULT NULL COMMENT '签署人证件脱敏',
    page_no         INT          NOT NULL DEFAULT 1 COMMENT '页码',
    pos_x           DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT 'X坐标(千分比)',
    pos_y           DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT 'Y坐标(千分比)',
    width           DECIMAL(5,2) DEFAULT 10.0 COMMENT '宽度(千分比)',
    height          DECIMAL(5,2) DEFAULT 10.0 COMMENT '高度(千分比)',
    sign_time       DATETIME     DEFAULT NULL COMMENT '签署时间',
    status          TINYINT      DEFAULT 1 COMMENT '状态: 1待签 2已签 3已失效',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同签章表';

-- 20. 合同附件表
DROP TABLE IF EXISTS contract_attachment;
CREATE TABLE contract_attachment (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '附件ID',
    contract_id BIGINT       NOT NULL COMMENT '合同ID',
    file_name   VARCHAR(200) NOT NULL COMMENT '文件名',
    object_key  VARCHAR(255) NOT NULL COMMENT '对象键',
    file_size   BIGINT       DEFAULT NULL COMMENT '文件大小(字节)',
    file_type   TINYINT      DEFAULT 3 COMMENT '类型: 1原件扫描 2补充协议 3其他',
    upload_by   BIGINT       DEFAULT NULL COMMENT '上传人',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同附件表';

-- ============================================================
-- 四、履约模块（3张）
-- ============================================================

-- 21. 履约节点表
DROP TABLE IF EXISTS contract_node;
CREATE TABLE contract_node (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '节点ID',
    contract_id     BIGINT       NOT NULL COMMENT '合同ID',
    template_node_id BIGINT      DEFAULT NULL COMMENT '来源模板节点ID',
    node_name       VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_type       TINYINT      DEFAULT 3 COMMENT '类型: 1付款 2交付 3其他',
    owner_id        BIGINT       DEFAULT NULL COMMENT '责任人ID',
    dept_id         BIGINT       DEFAULT NULL COMMENT '责任部门ID',
    due_date        DATE         NOT NULL COMMENT '截止日期',
    finish_standard VARCHAR(500) DEFAULT NULL COMMENT '完成标准',
    status          TINYINT      DEFAULT 0 COMMENT '状态: 0待执行 1进行中 2已完成 3已逾期',
    finish_time     DATETIME     DEFAULT NULL COMMENT '完成时间',
    is_on_time      TINYINT      DEFAULT NULL COMMENT '是否按时: 1按时 0逾期',
    overdue_days    INT          DEFAULT 0 COMMENT '逾期天数',
    sort            INT          DEFAULT 0 COMMENT '排序',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id),
    KEY idx_owner_id (owner_id),
    KEY idx_due_date (due_date),
    KEY idx_status_due (status, due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='履约节点表';

-- 22. 节点提醒记录表
DROP TABLE IF EXISTS contract_node_reminder;
CREATE TABLE contract_node_reminder (
    id                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '提醒ID',
    node_id             BIGINT       NOT NULL COMMENT '节点ID',
    remind_type         TINYINT      NOT NULL COMMENT '类型: 1系统消息 2邮件 3短信',
    remind_day_offset   TINYINT      NOT NULL COMMENT '偏移: -3前3天 0当天 1逾期',
    remind_date         DATETIME     DEFAULT NULL COMMENT '提醒时间',
    status              TINYINT      DEFAULT 0 COMMENT '状态: 0失败 1成功',
    retry_count         INT          DEFAULT 0 COMMENT '重试次数',
    error_msg           VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    create_time          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_node_type_offset (node_id, remind_type, remind_day_offset),
    KEY idx_remind_date (remind_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点提醒记录表';

-- 23. 履约问题表
DROP TABLE IF EXISTS contract_issue;
CREATE TABLE contract_issue (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '问题ID',
    contract_id     BIGINT       NOT NULL COMMENT '合同ID',
    node_id         BIGINT       DEFAULT NULL COMMENT '关联节点ID',
    title           VARCHAR(200) NOT NULL COMMENT '问题标题',
    description     TEXT         COMMENT '问题描述',
    impact          VARCHAR(500) DEFAULT NULL COMMENT '影响范围',
    attachment      JSON         DEFAULT NULL COMMENT '证明材料',
    status          TINYINT      DEFAULT 1 COMMENT '状态: 1待协调 2处理中 3已解决',
    handler_id      BIGINT       DEFAULT NULL COMMENT '处理人ID',
    resolution      TEXT         COMMENT '处理结果',
    resolved_time   DATETIME     DEFAULT NULL COMMENT '解决时间',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id),
    KEY idx_node_id (node_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='履约问题表';

-- ============================================================
-- 五、工单模块（4张）
-- ============================================================

-- 24. 售后工单表
DROP TABLE IF EXISTS service_order;
CREATE TABLE service_order (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    order_no            VARCHAR(30)   NOT NULL COMMENT '工单编号: WD-YYYYMM-0001',
    customer_id         BIGINT        NOT NULL COMMENT '客户ID',
    contact_id          BIGINT        DEFAULT NULL COMMENT '联系人ID',
    contact_name        VARCHAR(50)   DEFAULT NULL COMMENT '联系人快照',
    contact_phone_mask  VARCHAR(20)   DEFAULT NULL COMMENT '联系人电话脱敏',
    contract_id         BIGINT        DEFAULT NULL COMMENT '关联合同ID',
    order_ref_no        VARCHAR(50)   DEFAULT NULL COMMENT '关联订单号',
    customer_level_snap TINYINT       DEFAULT NULL COMMENT '客户层级快照',
    type                TINYINT       NOT NULL COMMENT '类型: 1履约问题 2产品故障 3服务咨询 4投诉建议 5其他',
    urgency             TINYINT       DEFAULT 1 COMMENT '紧急度: 1普通 2紧急 3特急',
    description         TEXT          NOT NULL COMMENT '问题描述(5W1H)',
    attachment          JSON          DEFAULT NULL COMMENT '附件',
    source              TINYINT       DEFAULT 4 COMMENT '来源: 1电话 2邮件 3微信 4系统 5其他',
    submitter_type      TINYINT       DEFAULT 1 COMMENT '提交人类型: 1内部员工 2客户本人',
    submitter_id        BIGINT        DEFAULT NULL COMMENT '提交人ID(内部)',
    submitter_name      VARCHAR(50)   DEFAULT NULL COMMENT '提交人姓名',
    assignee_id         BIGINT        DEFAULT NULL COMMENT '负责人ID',
    dept_id             BIGINT        DEFAULT NULL COMMENT '责任部门ID',
    status              TINYINT       DEFAULT 1 COMMENT '状态: 1待分配 2处理中 3待客户确认 4已闭环 5已驳回',
    assign_time         DATETIME      DEFAULT NULL COMMENT '分配时间',
    first_response_time DATETIME      DEFAULT NULL COMMENT '首次响应时间',
    first_response_way  TINYINT       DEFAULT NULL COMMENT '响应方式: 1电话 2短信 3系统消息',
    sla_deadline        DATETIME      DEFAULT NULL COMMENT '响应截止时间',
    finish_time         DATETIME      DEFAULT NULL COMMENT '完成时间',
    resolve_desc        TEXT          COMMENT '处理结果',
    confirm_status      TINYINT       DEFAULT NULL COMMENT '确认状态: 1待确认 2已确认 3待反馈',
    confirm_time        DATETIME      DEFAULT NULL COMMENT '客户确认时间',
    reject_reason       VARCHAR(500)  DEFAULT NULL COMMENT '驳回原因',
    archive_time        DATETIME      DEFAULT NULL COMMENT '归档时间',
    archive_by         BIGINT        DEFAULT NULL COMMENT '归档人',
    report_id           BIGINT        DEFAULT NULL COMMENT '关联报表ID',
    create_time          DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time          DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag            TINYINT       DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_customer_id (customer_id),
    KEY idx_assignee_id (assignee_id),
    KEY idx_status_create (status, create_time),
    KEY idx_sla_deadline (sla_deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后工单表';

-- 25. 工单流转日志表
DROP TABLE IF EXISTS service_order_log;
CREATE TABLE service_order_log (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    order_id        BIGINT       NOT NULL COMMENT '工单ID',
    from_status     TINYINT      DEFAULT NULL COMMENT '原状态',
    to_status       TINYINT      DEFAULT NULL COMMENT '新状态',
    action          VARCHAR(50)  NOT NULL COMMENT '操作: 分配/响应/处理/确认/驳回',
    operator_type   TINYINT      DEFAULT 1 COMMENT '操作人类型: 1内部 2客户 3系统',
    operator_id     BIGINT       DEFAULT NULL COMMENT '操作人ID',
    operator_name   VARCHAR(50)  DEFAULT NULL COMMENT '操作人姓名',
    content         VARCHAR(500) DEFAULT NULL COMMENT '操作内容',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流转日志表';

-- 26. 满意度评分表
DROP TABLE IF EXISTS satisfaction_score;
CREATE TABLE satisfaction_score (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评分ID',
    order_id        BIGINT       NOT NULL COMMENT '工单ID',
    customer_id     BIGINT       NOT NULL COMMENT '客户ID',
    assignee_id     BIGINT       NOT NULL COMMENT '负责人ID',
    score           TINYINT      NOT NULL COMMENT '评分: 1-5',
    dimension_json  JSON         COMMENT '评价维度数组',
    suggestion      VARCHAR(500) DEFAULT NULL COMMENT '具体建议',
    token_id        BIGINT       DEFAULT NULL COMMENT '访问令牌ID',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '评分时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_id (order_id),
    KEY idx_assignee_id (assignee_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='满意度评分表';

-- 27. C端访问令牌表
DROP TABLE IF EXISTS order_access_token;
CREATE TABLE order_access_token (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '令牌ID',
    order_id    BIGINT       DEFAULT NULL COMMENT '关联工单ID(建单令牌为空)',
    customer_id BIGINT       NOT NULL COMMENT '客户ID',
    token       VARCHAR(64)  NOT NULL COMMENT '令牌字符串',
    scope       INT          NOT NULL DEFAULT 7 COMMENT '权限位: 1查看 2确认 4评分 8建单',
    expire_time DATETIME     NOT NULL COMMENT '过期时间',
    used_flag   TINYINT      DEFAULT 0 COMMENT '是否已使用',
    use_time    DATETIME     DEFAULT NULL COMMENT '使用时间',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_token (token),
    KEY idx_order_id (order_id),
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='C端访问令牌表';

-- ============================================================
-- 六、AI与统计模块（3张）
-- ============================================================

-- 28. 知识库文档表
DROP TABLE IF EXISTS knowledge_document;
CREATE TABLE knowledge_document (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文档ID',
    title           VARCHAR(200) NOT NULL COMMENT '文档标题',
    category        VARCHAR(50)  NOT NULL COMMENT '分类: CONTRACT_LAW/RISK_GUIDE',
    object_key      VARCHAR(255) NOT NULL COMMENT '文件对象键',
    file_size       BIGINT       DEFAULT NULL COMMENT '文件大小',
    chunk_count     INT          DEFAULT 0 COMMENT '切分chunk数',
    status          TINYINT      DEFAULT 0 COMMENT '状态: 0解析中 1已入库 2失败',
    error_msg       VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    chroma_collection VARCHAR(100) DEFAULT NULL COMMENT 'Chroma集合名',
    create_by       BIGINT       DEFAULT NULL COMMENT '上传人',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库文档表';

-- 29. AI扫描记录表
DROP TABLE IF EXISTS ai_scan_record;
CREATE TABLE ai_scan_record (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    contract_id     BIGINT       NOT NULL COMMENT '合同ID',
    contract_version INT         DEFAULT 1 COMMENT '合同版本',
    risk_json       JSON         COMMENT '风险点JSON结果',
    model           VARCHAR(50)  DEFAULT NULL COMMENT '使用的模型',
    prompt_tokens   INT          DEFAULT NULL COMMENT '输入token数',
    completion_tokens INT        DEFAULT NULL COMMENT '输出token数',
    cost_ms         BIGINT       DEFAULT NULL COMMENT '耗时(毫秒)',
    create_by       BIGINT       DEFAULT NULL COMMENT '操作人',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '扫描时间',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI扫描记录表';

-- 30. 统计报表表
DROP TABLE IF EXISTS stat_report;
CREATE TABLE stat_report (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '报表ID',
    report_type VARCHAR(50)  NOT NULL COMMENT '类型: OVERVIEW/LAYER/CONTRACT_PERFORMANCE/ORDER_SLA/SATISFACTION',
    biz_period  VARCHAR(20)  NOT NULL COMMENT '统计周期: 如 2026-Q3',
    params_json JSON         COMMENT '查询参数',
    object_key  VARCHAR(255) DEFAULT NULL COMMENT '文件对象键',
    file_type   TINYINT      DEFAULT 1 COMMENT '文件类型: 1 Excel 2 PDF',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 1生成中 2成功 3失败',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_type_period (report_type, biz_period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计报表表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 建表完成: 共30张表
-- ============================================================

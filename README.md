# 顶钢 CRM - 初创公司客户关系与合同管理系统

## 项目结构

```
dinggang/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/dinggang/crm/
│   │   ├── DinggangCrmApplication.java   # 启动类
│   │   ├── common/                       # 公共组件
│   │   │   ├── config/                   # 配置类
│   │   │   ├── exception/               # 异常处理
│   │   │   ├── result/                   # 统一响应
│   │   │   └── utils/                    # 工具类
│   │   └── modules/                     # 业务模块
│   │       ├── customer/                # 客户模块
│   │       ├── system/                  # 系统/权限模块
│   │       ├── contract/                # 合同模块 (待开发)
│   │       ├── performance/              # 履约模块 (待开发)
│   │       ├── serviceorder/            # 工单模块 (待开发)
│   │       ├── stats/                    # 统计模块 (待开发)
│   │       └── ai/                      # AI助手 (待开发)
│   ├── src/main/resources/
│   │   ├── application.yml               # 配置文件
│   │   ├── db/schema.sql                 # 建表脚本 (30张表)
│   │   ├── db/data.sql                   # 初始化数据
│   │   ├── mapper/                      # MyBatis XML
│   │   └── bpmn/                        # Flowable流程定义
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                   # Vue3 前端 (待初始化)
├── db/                         # 数据库脚本备份
├── nginx/                      # Nginx配置
├── docker-compose.yml          # 一键启动
├── .env.example               # 环境变量示例
└── 开发设计文档.md             # 详细设计文档
```

## 快速启动

### 方式一：Docker Compose（推荐）

```bash
# 1. 复制环境变量
cp .env.example .env
# 编辑 .env 修改密码等配置

# 2. 一键启动所有服务
docker-compose up -d

# 3. 查看状态
docker-compose ps
```

### 方式二：本地开发

```bash
# 1. 启动MySQL和Redis (可以用Docker)
docker-compose up -d mysql redis minio chroma

# 2. 后端启动
cd backend
mvn spring-boot:run

# 3. 访问
# 接口文档: http://localhost:8080/api/doc.html
# 默认账号: admin / admin123
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Sa-Token + Flowable |
| 前端 | Vue 3 + Element Plus + ECharts (待搭建) |
| 数据库 | MySQL 8 + Redis 7 |
| 存储 | MinIO (对象存储) + Chroma (向量库) |
| 部署 | Docker Compose + Nginx |

## 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 系统管理员 |

## 开发计划

- [x] W1: 项目骨架 + 数据库设计 + 登录认证
- [ ] W2: 客户模块 + 合同模板库
- [ ] W3: 合同审批流 + 履约节点 + 签章
- [ ] W4: 工单模块 + 统计报表
- [ ] W5: AI助手 + 测试优化 + 部署

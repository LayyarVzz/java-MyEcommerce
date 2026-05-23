你需要使用中文和用户交互。

# 电商网站项目 - MyEcommerce

## 项目概述

MyEcommerce 是一个基于 Spring Boot 的在线购物网站系统，使用 Thymeleaf 模板引擎和 Bootstrap 5 作为前端框架，使用 MySQL 持久化数据，并通过 Spring Security 管理登录认证和角色权限。

当前项目包含用户端购物流程、后台商品/订单/客户管理、销售报表、用户活动记录、客户画像和商品推荐能力。

## 技术栈

- 后端：Java 17 + Spring Boot 3.5.14
- 数据库：MySQL 8
- 数据访问：Spring Data JPA + Hibernate
- 前端：Thymeleaf + Bootstrap 5 + HTML5
- 安全：Spring Security + BCrypt
- 构建：Maven Wrapper
- 部署：Docker + Docker Compose

## 项目结构

### 后端 Java 源码

位置：`src/main/java/com/example/myecommerce/`

主要包结构：

- `config/` - 配置类
    - `SecurityConfig.java` - Spring Security 配置
    - `PasswordEncoderConfig.java` - 密码加密配置
- `controller/` - 控制器层
    - `LoginController.java` - 登录相关
    - `RegisterController.java` - 注册相关
    - `ProductController.java` - 商品列表、搜索、分类、推荐
    - `CartController.java` - 购物车
    - `OrderController.java` - 用户订单
    - `AddressController.java` - 收货地址
    - `CustomerController.java` - 客户管理
    - `ReportController.java` - 销售报表
    - `ProductAdminController.java` - 商品管理（后台）
    - `OrderAdminController.java` - 订单管理（后台）
- `entity/` - JPA 实体类
    - `User.java` - 用户
    - `Product.java` - 商品
    - `Order.java` - 订单
    - `OrderItem.java` - 订单明细
    - `CartItem.java` - 购物车项
    - `Address.java` - 收货地址
    - `UserActivity.java` - 用户活动记录
- `repository/` - 数据访问层（Spring Data JPA Repository）
- `service/` - 业务逻辑层
    - `UserService.java` - 用户和角色
    - `ProductService.java` - 商品
    - `CartService.java` - 购物车
    - `OrderService.java` - 订单
    - `AddressService.java` - 地址
    - `ReportService.java` - 报表
    - `AnalyticsService.java` - 客户分析
    - `RecommendationService.java` - 商品推荐
    - `UserActivityService.java` - 用户活动记录
    - `MailService.java` - 邮件发送
- `util/` - 通用工具
    - `RequestUtils.java` - 请求信息工具

### 前端页面

位置：`src/main/resources/templates/`

用户页面：

- `login.html` - 登录页面
- `register.html` - 注册页面
- `products.html` - 商品列表页
- `cart.html` - 购物车
- `checkout.html` - 结算页面
- `order-history.html` - 订单历史
- `order-detail.html` - 订单详情
- `address-list.html` - 地址列表
- `address-form.html` - 地址表单

管理后台页面：

- `admin/product-list.html` - 商品列表（后台）
- `admin/product-form.html` - 商品表单
- `admin/order-list.html` - 订单列表（后台）
- `admin/order-detail.html` - 订单详情（后台）
- `admin/customer-list.html` - 客户列表
- `admin/customer-detail.html` - 客户详情、活动日志、客户画像
- `admin/sales-report.html` - 销售报表
- `fragments/navbar.html` - 公共导航栏

静态资源：

- `src/main/resources/static/upload/` - 上传的商品图片
- `src/main/resources/static/favicon.ico` - 网站图标

配置和数据：

- `src/main/resources/application.yaml` - Spring Boot 主配置
- `src/main/resources/data.sql` - 初始化数据脚本
- `backup.sql` - 数据库备份
- `products.csv` - 商品数据文件

## 访问路径

### 用户端

- `/products` - 商品列表（首页）
- `/login` - 登录
- `/register` - 注册
- `/cart` - 购物车
- `/cart/checkout` - 结算
- `/orders` - 订单历史
- `/orders/{id}` - 订单详情
- `/addresses` - 收货地址列表
- `/addresses/add` - 添加地址

### 管理后台

- `/admin/products` - 商品管理，需要 `ADMIN` 或 `SALES` 角色
- `/admin/orders` - 订单管理，需要 `ADMIN` 或 `SALES` 角色
- `/admin/reports` - 销售报表，需要 `ADMIN` 或 `SALES` 角色
- `/admin/customers` - 客户管理，仅 `ADMIN` 角色

## 运行和配置

用户电脑是 Windows 系统，终端命令优先使用 PowerShell 写法。

常用命令：

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd clean package
java -jar target\MyEcommerce-1.0.0.jar
docker compose up -d mysql
docker compose up -d --build
```

数据库配置位于 `src/main/resources/application.yaml`：

- 默认端口：`8080`
- 默认数据库：`jdbc:mysql://localhost:3306/ecommerce_db`
- 默认账号：`root`
- 默认密码：`mysql`
- JPA 建表策略：`ddl-auto: update`
- Thymeleaf 缓存：关闭

如果使用 Docker Compose 同时运行应用和 MySQL，应用容器内数据库地址需要使用 `mysql` 服务名，而不是 `localhost`。

## 开发和维护约定

1. 与用户沟通必须使用中文。
2. 修改前先阅读现有代码和模板，保持当前项目风格。
3. 当前仓库可能存在用户未提交改动，不能回滚或覆盖与任务无关的文件。
4. 前端美化任务优先修改 `src/main/resources/templates/` 和 `src/main/resources/static/`，不要为了纯样式调整改动 Java 业务逻辑。
5. 如果用户明确要求后端功能或缺陷修复，可以修改 Java 源码，但要同步检查控制器、服务层、实体、Repository 和模板入口。
6. Thymeleaf 页面中要保留必要的 `th:*` 绑定、Spring Security 权限片段和表单字段名，避免破坏后端数据绑定。
7. Bootstrap 5 已集成，优先使用现有组件和工具类：导航栏、卡片、表单、表格、按钮、网格和响应式工具。
8. 涉及订单金额、库存扣减、客户余额、角色权限、密码、邮件、用户活动记录时要格外谨慎，修改后需要做对应验证。
9. 不要提交真实数据库密码、邮箱授权码、生产环境密钥或个人敏感信息；如发现硬编码凭据，优先提示用户并建议改为环境变量。
10. 文档更新需要保持 README.md 面向项目使用者，AGENTS.md 面向后续协作代理。

## 前端美化原则

- 保持整体风格一致。
- 使用 Bootstrap 5 的组件和工具类。
- 优化页面布局、层次、留白和视觉反馈。
- 保证桌面端和移动端响应式表现。
- 避免移除 Thymeleaf 数据绑定、错误提示和权限展示逻辑。

## 最近的变更

- 用户要求美化前端页面，提升用户体验和视觉效果。
- 项目中已加入商品搜索/分类、推荐服务、客户分析、用户活动记录、销售角色和销售报表相关能力。
- README.md 和 AGENTS.md 已更新为当前项目结构、运行方式和维护约定。


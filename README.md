# MyEcommerce

MyEcommerce 是一个基于 Spring Boot 的在线购物网站系统，前端使用 Thymeleaf 模板和 Bootstrap 5，后端使用 Spring Data JPA、Spring Security 和 MySQL。项目包含用户端购物流程、销售工作台商品与订单处理、管理后台销售账号与经营报表管理、用户行为记录与商品推荐等功能。

## 技术栈

- 后端：Java 17、Spring Boot 3.5.14
- 数据访问：Spring Data JPA、Hibernate
- 安全认证：Spring Security、BCrypt
- 数据库：MySQL 8
- 前端：Thymeleaf、Bootstrap 5、HTML5
- 构建：Maven Wrapper
- 部署：Docker、Docker Compose

## 主要功能

### 用户端

- 用户注册、登录、退出和 Remember Me
- 商品列表、搜索、分类筛选和个性化推荐
- 购物车管理和结算下单
- 订单历史和订单详情
- 收货地址管理

### 管理后台

- 销售账号管理：新增、删除销售人员账号和重置密码
- 用户与销售信息查看：客户列表、客户详情、角色/余额维护
- 经营监控：用户浏览、下单、购物车、后台操作等活动记录
- 销售报表：指定日期区间的销售统计和可视化页面

### 销售工作台

- 商品管理：新增、编辑、上下架、库存维护
- 订单处理：查看订单、处理订单状态
- 销售状态监控：商品库存、待处理订单和经营摘要

## 项目结构

```text
.
├── pom.xml
├── mvnw / mvnw.cmd
├── Dockerfile
├── docker-compose.yml
├── backup.sql
├── products.csv
└── src
    └── main
        ├── java/com/example/myecommerce
        │   ├── config/        # Spring Security、密码编码等配置
        │   ├── controller/    # MVC 控制器
        │   ├── entity/        # JPA 实体
        │   ├── repository/    # Spring Data JPA Repository
        │   ├── service/       # 业务逻辑、报表、推荐、邮件、用户行为
        │   └── util/          # 请求工具等通用方法
        └── resources
            ├── application.yaml
            ├── data.sql
            ├── docs/
            ├── static/
            └── templates/
```

## 页面模板

### 用户页面

- `src/main/resources/templates/login.html`：登录
- `src/main/resources/templates/register.html`：注册
- `src/main/resources/templates/products.html`：商品列表
- `src/main/resources/templates/cart.html`：购物车
- `src/main/resources/templates/checkout.html`：结算
- `src/main/resources/templates/order-history.html`：订单历史
- `src/main/resources/templates/order-detail.html`：订单详情
- `src/main/resources/templates/address-list.html`：地址列表
- `src/main/resources/templates/address-form.html`：地址表单

### 后台页面

- `src/main/resources/templates/admin/customer-list.html`：客户列表
- `src/main/resources/templates/admin/customer-detail.html`：客户详情
- `src/main/resources/templates/admin/sales-report.html`：销售报表
- `src/main/resources/templates/fragments/navbar.html`：公共导航栏

### 销售工作台页面

- `src/main/resources/templates/sales/product-list.html`：商品列表
- `src/main/resources/templates/sales/product-form.html`：商品表单
- `src/main/resources/templates/sales/order-list.html`：订单列表
- `src/main/resources/templates/sales/order-detail.html`：订单详情

## 配置说明

主配置文件位于 `src/main/resources/application.yaml`。

默认配置：

- 服务端口：`8080`
- 数据库：`jdbc:mysql://localhost:3306/ecommerce_db`
- 数据库账号：`root`
- 数据库密码：`mysql`
- JPA：`ddl-auto: update`
- Thymeleaf：关闭模板缓存，便于开发调试

注意事项：

- 本地运行前请确认 MySQL 已创建 `ecommerce_db` 数据库，或使用 Docker Compose 启动 MySQL。
- 邮件服务默认使用 QQ SMTP。邮件授权码请放在项目根目录的 `.env` 文件中，例如 `MAIL_PASSWORD=你的授权码`；该文件已被 Git 忽略。仓库只保留 `.env.example` 作为模板。遇到本机代理或 DNS 导致 `smtp.qq.com` 解析到 `198.18.*` 假 IP、JavaMail 报 `SSL peer shut down incorrectly` 时，可在 `.env` 中覆盖 `MAIL_HOST`、`MAIL_PORT`、`MAIL_USERNAME` 和 `MAIL_NICKNAME`，或调整代理规则让 SMTP 直连。
- 如果使用 Docker Compose 同时运行应用和 MySQL，应用容器内的数据库地址应使用 `mysql` 服务名，例如 `jdbc:mysql://mysql:3306/ecommerce_db`。

## 本地运行

Windows PowerShell 下可以使用 Maven Wrapper：

```powershell
.\mvnw.cmd spring-boot:run
```

打包：

```powershell
.\mvnw.cmd clean package
```

运行打包后的 Jar：

```powershell
java -jar target\MyEcommerce-1.0.0.jar
```

访问地址：

- 本地：http://localhost:8080/products
- 登录页：http://localhost:8080/login
- 注册页：http://localhost:8080/register
- 部署访问：http://134.175.18.182:8080/login

## Docker 运行

启动 MySQL：

```powershell
docker compose up -d mysql
```

启动完整服务：

```powershell
docker compose up -d --build
```

查看日志：

```powershell
docker compose logs -f app
```

停止服务：

```powershell
docker compose down
```

## 访问路径

### 用户端

- `/`：需要认证后访问，建议从 `/products` 进入
- `/products`：商品列表首页
- `/login`：登录
- `/register`：注册
- `/cart`：购物车
- `/cart/checkout`：结算
- `/orders`：订单历史
- `/orders/{id}`：订单详情
- `/addresses`：收货地址列表
- `/addresses/add`：新增收货地址

### 管理后台

- `/admin/dashboard`：管理员控制台，仅允许 `ADMIN`
- `/admin/reports`：销售报表，仅允许 `ADMIN`
- `/admin/customers`：用户与销售账号管理，仅允许 `ADMIN`
- `/admin/activities`：数据日志，仅允许 `ADMIN`

### 销售工作台

- `/sales/dashboard`：销售工作台，仅允许 `SALES`
- `/sales/products`：商品维护，仅允许 `SALES`
- `/sales/orders`：订单处理，仅允许 `SALES`
- `/sales/activities`：用户浏览/购买日志，仅允许 `SALES`


# MyEcommerce

MyEcommerce 是一个基于 Spring Boot 的在线购物网站系统。项目前端使用 Thymeleaf 和 Bootstrap 5，后端使用 Spring Data JPA、Spring Security、Spring Boot Mail 和 MySQL，支持用户购物流程、商品详情与评论、销售工作台、管理后台、用户行为记录、客户画像、销售报表和商品推荐。

## 技术栈

- 后端：Java 17、Spring Boot 3.5.14
- 数据访问：Spring Data JPA、Hibernate
- 安全认证：Spring Security、BCrypt、Remember Me
- 数据库：MySQL 8
- 前端：Thymeleaf、Bootstrap 5、HTML5、自定义 CSS/JS
- 邮件：Spring Boot Mail，默认 QQ SMTP
- 构建：Maven Wrapper
- 部署：Docker、Docker Compose

## 主要功能

### 用户端

- 用户注册、登录、退出和 Remember Me
- 商品列表、搜索、分类筛选和个性化推荐
- 商品详情、相关商品、评论筛选、发布评论和评论点赞
- 购物车管理、结算下单和邮件确认
- 订单历史、订单详情和收货地址管理
- 用户登录、浏览、停留时长、加购、下单等行为记录

### 管理后台

- 管理员仪表盘：用户、商品、订单、活动与近 30 天经营摘要
- 销售账号管理：新增、删除销售账号和重置密码
- 用户管理：客户列表、客户详情、角色/余额维护
- 客户分析：用户画像、偏好分类、购买力与活动日志
- 经营监控：登录、浏览、下单、购物车、后台操作等活动记录
- 销售报表：指定日期区间的销售统计和报表页面

### 销售工作台

- 销售仪表盘：商品数量、低库存商品、待处理订单和经营摘要
- 商品管理：新增、编辑、下架、库存维护和分类维护
- 订单处理：查看订单、查看订单详情、更新订单状态
- 活动日志：查看用户浏览/购买日志，辅助销售跟进

## 项目结构

```text
.
├── pom.xml
├── mvnw / mvnw.cmd
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── backup.sql
├── products.csv
├── Tasks.md
└── src
    └── main
        ├── java/com/example/myecommerce
        │   ├── config/        # Spring Security、密码编码等配置
        │   ├── controller/    # MVC 控制器
        │   ├── entity/        # JPA 实体
        │   ├── repository/    # Spring Data JPA Repository
        │   ├── service/       # 业务逻辑、报表、推荐、邮件、图片本地化
        │   └── util/          # 请求工具等通用方法
        └── resources
            ├── application.yaml
            ├── data.sql
            ├── static/
            │   ├── css/
            │   ├── js/
            │   └── upload/
            └── templates/
                ├── admin/
                ├── fragments/
                └── sales/
```

## 页面模板

### 用户页面

- `src/main/resources/templates/login.html`：登录
- `src/main/resources/templates/register.html`：注册
- `src/main/resources/templates/products.html`：商品列表
- `src/main/resources/templates/product-detail.html`：商品详情
- `src/main/resources/templates/product-comments.html`：全部评论
- `src/main/resources/templates/cart.html`：购物车
- `src/main/resources/templates/checkout.html`：结算
- `src/main/resources/templates/order-history.html`：订单历史
- `src/main/resources/templates/order-detail.html`：订单详情
- `src/main/resources/templates/address-list.html`：地址列表
- `src/main/resources/templates/address-form.html`：地址表单

### 管理后台页面

- `src/main/resources/templates/admin/dashboard.html`：管理员控制台
- `src/main/resources/templates/admin/customer-list.html`：客户与销售账号列表
- `src/main/resources/templates/admin/customer-detail.html`：客户详情、客户画像、活动日志
- `src/main/resources/templates/admin/sales-report.html`：销售报表
- `src/main/resources/templates/admin/activity-log.html`：数据日志
- `src/main/resources/templates/fragments/admin-shell.html`：管理后台布局
- `src/main/resources/templates/fragments/navbar.html`：用户端公共导航栏

### 销售工作台页面

- `src/main/resources/templates/sales/dashboard.html`：销售控制台
- `src/main/resources/templates/sales/product-list.html`：商品列表
- `src/main/resources/templates/sales/product-form.html`：商品表单
- `src/main/resources/templates/sales/order-list.html`：订单列表
- `src/main/resources/templates/sales/order-detail.html`：订单详情
- `src/main/resources/templates/sales/activity-log.html`：用户浏览/购买日志
- `src/main/resources/templates/sales/sales-report.html`：销售报表视图模板
- `src/main/resources/templates/fragments/sales-shell.html`：销售工作台布局

### 静态资源

- `src/main/resources/static/css/storefront.css`：用户端样式
- `src/main/resources/static/css/backoffice.css`：后台工作台样式
- `src/main/resources/static/css/confirm-dialog.css`：确认弹窗样式
- `src/main/resources/static/js/platform-confirm.js`：通用确认弹窗
- `src/main/resources/static/js/product-comments.js`：商品评论交互
- `src/main/resources/static/upload/`：内置静态商品图片
- `upload/products`：运行期商品图片目录，对外路径为 `/upload/products`

## 配置说明

主配置文件位于 `src/main/resources/application.yaml`。

当前默认配置：

- 服务端口：`8080`
- 数据库地址：`jdbc:mysql://mysql:3306/ecommerce_db`（Docker 部署，通过服务名访问）
- 数据库账号：`root`
- 数据库密码：`mysql`
- JPA：`ddl-auto: update`
- Thymeleaf：关闭模板缓存，便于开发调试
- 商品图片本地化：启动时开启，运行期目录为 `upload/products`

### 本地 IDE 运行 vs Docker 部署的数据库地址

| 运行方式 | 数据库 URL | 原因 |
|---------|-----------|------|
| Docker Compose 部署 | `jdbc:mysql://mysql:3306/ecommerce_db` | app 和 MySQL 在同一个 Docker 网络，服务名 `mysql` 即为 hostname |
| 本地 IDE 运行 | `jdbc:mysql://localhost:3306/ecommerce_db` | app 不在 Docker 网络内，通过端口映射 `localhost:3306` 连接 |

本地 IDE 运行时，先确保 MySQL 容器已启动（`docker compose up -d mysql`），然后通过环境变量覆盖数据库地址：

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/ecommerce_db"
.\mvnw.cmd spring-boot:run
```

`.env` 示例：

```properties
MAIL_USERNAME=your_email@qq.com
MAIL_PASSWORD=your_smtp_password
```

## 本地 IDE 运行

适合开发调试，直接通过 IDEA 或命令行启动 Spring Boot。

先启动 MySQL 容器（端口已映射到 `localhost:3306`）：

```powershell
docker compose up -d mysql
```

然后运行应用：

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/ecommerce_db"
.\mvnw.cmd spring-boot:run
```

打包：

```powershell
.\mvnw.cmd clean package
```

运行打包后的 Jar：

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/ecommerce_db"
java -jar target\MyEcommerce-1.0.0.jar
```

访问地址：

- 商品首页：http://localhost:8080/products
- 登录页：http://localhost:8080/login
- 注册页：http://localhost:8080/register
- 管理员控制台：http://localhost:8080/admin/dashboard
- 销售工作台：http://localhost:8080/sales/dashboard

## Docker 部署

### 本地部署

```powershell
# 构建并启动（首次或代码有变更时）
docker compose up --build -d

# 仅启动（已有镜像，代码未变更）
docker compose up -d

# 仅构建镜像，不启动容器
docker compose build

# 单独启动 MySQL
docker compose up -d mysql

# 查看应用日志
docker compose logs -f app

# 停止服务
docker compose down
```

### 服务器部署

将以下文件上传到服务器同一目录：

| 文件 | 说明 |
|------|------|
| `docker-compose.yml` | 编排文件 |
| `Dockerfile` | app 镜像构建文件 |
| `.env` | 邮件等环境变量 |
| `upload/` | 商品图片目录（如有） |
| `pom.xml`、`src/` | 源码（如需在服务器上构建） |

然后在服务器上执行：

```powershell
# 构建并启动
docker compose up --build -d

# 查看日志确认启动成功
docker compose logs -f app
```

也可以通过导出镜像的方式部署（适合服务器不安装 Maven 的场景）：

```powershell
# 本地导出镜像
docker save myecommerce-app -o myecommerce-app.tar

# 上传到服务器后导入
docker load -i myecommerce-app.tar

# 启动（MySQL 会自动从 Docker Hub 拉取）
docker compose up -d
```

Docker Compose 会创建 MySQL 数据卷 `mysql-data` 用于持久化数据库，商品图片通过 `./upload` 目录挂载到容器。

### 访问地址

| 环境 | 地址 |
|------|------|
| 本地 | http://localhost:8080 |
| 服务器 | http://134.175.18.182:8080 |

各页面路径见下方[访问路径](#访问路径)。

## 访问路径

> **本地**: `http://localhost:8080`　|　**服务器**: `http://134.175.18.182:8080`

### 用户端

| 路径 | 说明 |
|------|------|
| `/products` | 商品列表首页 |
| `/products/{id}` | 商品详情 |
| `/products/{id}/comments` | 商品全部评论 |
| `/login` | 登录页 |
| `/register` | 注册页 |
| `/cart` | 购物车，仅 `USER` |
| `/cart/checkout` | 结算，仅 `USER` |
| `/orders` | 订单历史，仅 `USER` |
| `/orders/{id}` | 订单详情，仅 `USER` |
| `/addresses` | 收货地址列表，仅 `USER` |
| `/addresses/add` | 新增收货地址，仅 `USER` |

### 管理后台

| 路径 | 说明 |
|------|------|
| `/admin/dashboard` | 管理员控制台，仅 `ADMIN` |
| `/admin/customers` | 用户与销售账号管理，仅 `ADMIN` |
| `/admin/customers/{id}` | 客户详情、客户画像和活动日志，仅 `ADMIN` |
| `/admin/reports` | 销售报表，仅 `ADMIN` |
| `/admin/activities` | 数据日志，仅 `ADMIN` |

### 销售工作台

| 路径 | 说明 |
|------|------|
| `/sales/dashboard` | 销售工作台，仅 `SALES` |
| `/sales/products` | 商品维护，仅 `SALES` |
| `/sales/products/add` | 新增商品，仅 `SALES` |
| `/sales/products/edit/{id}` | 编辑商品，仅 `SALES` |
| `/sales/orders` | 订单处理，仅 `SALES` |
| `/sales/orders/{id}` | 订单详情，仅 `SALES` |
| `/sales/activities` | 用户浏览/购买日志，仅 `SALES` |

## 预置账号

密码统一为 `123456`。

| 角色 | 账号 | 权限概述 |
|------|------|---------|
| 普通用户 (USER) | `customer` | 浏览商品、加入购物车、下单结算、管理收货地址、发布评论和点赞 |
| 测试用户 (USER) | `testuser` | 同上，用于测试 |
| 销售员 (SALES) | `saleuser` | 商品管理（新增/编辑/下架）、订单处理、销售数据查看、用户活动日志 |
| 管理员 (ADMIN) | `admin` | 用户与销售账号管理、客户画像分析、销售报表、经营监控、系统日志 |

## 权限说明

- 未登录用户可以浏览商品列表、商品详情、商品评论、登录页和注册页。
- `USER` 角色可以使用购物车、结算、订单、地址、评论发布/点赞和浏览时长记录。
- `SALES` 角色负责商品维护、订单处理、销售控制台和用户活动日志。
- `ADMIN` 角色负责销售账号管理、客户信息、经营监控、客户画像和销售报表。
- `/admin/products/**` 和 `/admin/orders/**` 已在安全配置中拒绝访问，商品维护和订单处理统一放在销售工作台。

## 验证命令

运行测试：

```powershell
.\mvnw.cmd test
```

完整打包验证：

```powershell
.\mvnw.cmd clean package
```

# 项目代码说明

## 项目概述
基于 SpringBoot 的在线购物网站系统，使用 Thymeleaf 模板引擎和 Bootstrap 5 作为前端框架。

## 目录结构说明

### Java 源码结构

**位置**: `java/com/example/myecommerce/`

-  **`controller/`**
  控制器层，处理 HTTP 请求和响应
  - `LoginController.java` - 登录功能
  - `RegisterController.java` - 用户注册
  - `ProductController.java` - 商品展示
  - `CartController.java` - 购物车操作
  - `OrderController.java` - 订单管理
  - `AddressController.java` - 收货地址
  - `CustomerController.java` - 用户信息
  - `ReportController.java` - 报表统计
  - `ProductAdminController.java` - 商品管理（后台）
  - `OrderAdminController.java` - 订单管理（后台）

-  **`entity/`**
  实体类，对应数据库表结构
  - `User.java` - 用户实体
  - `Product.java` - 商品实体
  - `Order.java` - 订单实体
  - `OrderItem.java` - 订单明细
  - `CartItem.java` - 购物车项
  - `Address.java` - 收货地址
  - `UserActivity.java` - 用户活动记录

-  **`repository/`**
  数据访问层，使用 Spring Data JPA
  - 数据库操作接口，继承 JpaRepository

-  **`service/`**
  业务逻辑层
  - 实现具体的业务逻辑

-  **`config/`**
  配置类
  - `SecurityConfig.java` - Spring Security 安全配置
  - `PasswordEncoderConfig.java` - 密码加密配置

### 前端页面结构

**位置**: `resources/templates/`

-  **`用户端页面`**
  - `login.html` - 登录页面
  - `register.html` - 注册页面
  - `products.html` - 商品列表（首页）
  - `cart.html` - 购物车
  - `checkout.html` - 结算页面
  - `order-history.html` - 订单历史
  - `order-detail.html` - 订单详情
  - `address-list.html` - 地址列表
  - `address-form.html` - 地址表单

-  **`管理后台页面`**
  需要 ADMIN 角色权限
  - `admin/product-list.html` - 商品列表（后台）
  - `admin/product-form.html` - 商品表单
  - `admin/order-list.html` - 订单列表
  - `admin/order-detail.html` - 订单详情
  - `admin/customer-list.html` - 客户列表
  - `admin/customer-detail.html` - 客户详情
  - `admin/sales-report.html` - 销售报表

### 静态资源

-  **`static/upload/`**
  上传的商品图片存放目录

-  **`static/favicon.ico`**
  网站图标

### 配置文件

-  **`application.yaml`**
  主配置文件，包含：
  - MySQL 数据库连接配置
  - JPA/Hibernate 配置
  - Thymeleaf 模板配置（关闭缓存）

## 主要功能

1. **用户功能**
   - 用户注册/登录
   - 浏览商品列表
   - 购物车管理
   - 订单提交和管理
   - 收货地址管理

2. **后台管理功能**
   - 商品管理（增删改查）
   - 订单管理
   - 客户管理
   - 销售报表统计

## 技术栈

- 后端：SpringBoot + Spring Data JPA + Spring Security
- 数据库：MySQL
- 前端：Thymeleaf + Bootstrap 5 + HTML5
- 模板引擎：Thymeleaf

## 如何运行

1. 配置数据库连接（修改 `application.yaml`）
2. 运行主类 `MyEcommerceApplication.java`
3. 访问应用：http://134.175.18.182:8080/login

## 默认访问路径

- 首页：/products
- 登录：/login
- 注册：/register
- 后台：/admin/products（需要管理员权限）

## 作者

姓名：黄子能

学号：202330450691

-- MySQL dump 10.13  Distrib 8.0.39, for Linux (x86_64)
--
-- Host: localhost    Database: ecommerce_db
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `ecommerce_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ecommerce_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `ecommerce_db`;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `contact_name` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_default` bit(1) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1fa36y2oqhao3wgg2rw1pi459` (`user_id`),
  CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,'SCUT','testUser','2025-12-27 06:31:03.546015',_binary '','190xxx',1),(2,'SCUT','layyar','2025-12-27 07:47:15.255307',_binary '','190xxx',3),(3,'SCUT 2','testUser1','2025-12-27 08:57:24.558351',_binary '\0','190xxx',1),(4,'SCUT 3','testUser2','2025-12-27 08:57:43.104809',_binary '\0','190xxx',1),(5,'111','customer','2026-05-22 19:57:29.479112',_binary '','111',6);
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  KEY `FK709eickf3kc0dujx3ub9i7btf` (`user_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK709eickf3kc0dujx3ub9i7btf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (12,1,1,6),(14,1,6,6),(15,1,23,6),(16,1,2,6),(17,1,33,1),(18,1,3,1);
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `price` decimal(38,2) NOT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1299.00,1,1,3),(2,1688.00,1,2,6),(3,2999.00,1,3,1),(4,1899.00,1,4,2),(5,2999.00,1,5,1),(6,1688.00,1,6,6),(7,1688.00,1,7,6),(8,2999.00,1,8,1),(9,1688.00,1,8,6),(10,1299.00,1,8,23),(11,1899.00,1,8,2);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contact_name` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(255) DEFAULT NULL,
  `delivery_address` varchar(255) DEFAULT NULL,
  `order_date` datetime(6) NOT NULL,
  `status` varchar(255) NOT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'testUser','190xxx','SCUT','2025-12-27 06:31:22.087748','已取消',1299.00,1),(2,'testUser','190xxx','SCUT','2025-12-27 06:38:18.171116','已取消',1688.00,1),(3,'testUser','190xxx','SCUT','2025-12-27 06:41:12.594301','已确认',2999.00,1),(4,'testUser','190xxx','SCUT','2025-12-27 06:41:19.144459','已确认',1899.00,1),(5,'testUser','190xxx','SCUT','2025-12-27 06:41:39.355670','已取消',2999.00,1),(6,'testUser','190xxx','SCUT','2025-12-27 07:43:25.548673','已取消',1688.00,1),(7,'layyar','190xxx','SCUT','2025-12-27 07:47:19.374036','待处理',1688.00,3),(8,'customer','111','111','2026-05-22 19:57:37.383262','待处理',7885.00,6);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_comment_likes`
--

DROP TABLE IF EXISTS `product_comment_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_comment_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `comment_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKk4jl5dk3tfdlunxqrbc0ayxhy` (`comment_id`,`user_id`),
  KEY `FKrjflsia0rrje5cmi1yh0nk73x` (`user_id`),
  CONSTRAINT `FK7msy0ahe65p0l03mknhvf9wop` FOREIGN KEY (`comment_id`) REFERENCES `product_comments` (`id`),
  CONSTRAINT `FKrjflsia0rrje5cmi1yh0nk73x` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_comment_likes`
--

LOCK TABLES `product_comment_likes` WRITE;
/*!40000 ALTER TABLE `product_comment_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_comment_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_comments`
--

DROP TABLE IF EXISTS `product_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(500) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `like_count` int NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `rating` enum('BAD','GOOD','NEUTRAL') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlvw9kwav1pell1wg6xo0dmme6` (`product_id`),
  KEY `FKgl4kke0pjaht09vx7wilce0n0` (`user_id`),
  CONSTRAINT `FKgl4kke0pjaht09vx7wilce0n0` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKlvw9kwav1pell1wg6xo0dmme6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_comments`
--

LOCK TABLES `product_comments` WRITE;
/*!40000 ALTER TABLE `product_comments` DISABLE KEYS */;
INSERT INTO `product_comments` VALUES (1,'测试','2026-05-25 12:39:54.164086',0,3,6,NULL),(2,'111','2026-05-25 12:40:03.296867',0,3,6,NULL),(3,'test','2026-05-25 12:55:51.129046',0,3,6,NULL),(4,'test','2026-05-25 12:56:19.233039',0,3,6,NULL),(5,'Codex AJAX 中评验证 1779686770584','2026-05-25 13:26:10.955921',0,1,7,'NEUTRAL');
/*!40000 ALTER TABLE `product_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `discontinued` bit(1) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `stock` int DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'高性能智能手机，8GB+256GB',_binary '\0','/upload/products/product-1.jpg','小米手机',2999.00,99,'手机数码'),(2,'10.4英寸大屏，学习办公神器',_binary '\0','/upload/products/product-2.webp','华为平板',1899.00,47,'手机数码'),(3,'无线蓝牙耳机，降噪功能',_binary '\0','/upload/products/product-3.webp','苹果耳机',1299.00,200,'手机数码'),(4,'轻薄本，16GB+512GB',_binary '\0','/upload/products/product-4.jpg','联想笔记本',4999.00,30,'电脑办公'),(5,'高清航拍，便携折叠',_binary '\0','/upload/products/product-5.png','大疆无人机',3699.00,20,'手机数码'),(6,'测试商品，请勿购买！',_binary '\0','/upload/products/product-6.jpg','佳能',1688.00,88,'综合商品'),(7,'钛金属设计，A17 Pro芯片，专业级摄像系统',_binary '\0','/upload/products/product-7.jpg','iPhone 15 Pro',7999.00,50,'手机数码'),(8,'AI智能手机，5000万像素，超视觉夜拍',_binary '\0','/upload/products/product-8.png','三星Galaxy S24',5999.00,80,'手机数码'),(9,'哈苏影像，超光影三主摄，100W超级闪充',_binary '\0','/upload/products/product-9.jpg','OPPO Find X7',4499.00,60,'手机数码'),(10,'蔡司影像，天玑9300旗舰芯片，120W双芯闪充',_binary '\0','/upload/products/product-10.jpg','vivo X100',4299.00,70,'手机数码'),(11,'13.6英寸，8GB+256GB，午夜色',_binary '\0','/upload/products/product-11.jpg','MacBook Air M2',8999.00,25,'电脑办公'),(12,'13.4英寸轻薄本，Intel i7处理器，16GB+512GB',_binary '\0','/upload/products/product-12.webp','戴尔XPS 13',6999.00,30,'电脑办公'),(13,'14英寸触控屏，12代酷睿，16GB+512GB',_binary '\0','/upload/products/product-13.webp','华为MateBook 14',5499.00,40,'电脑办公'),(14,'14英寸商务本，碳纤维机身，16GB+1TB',_binary '\0','/upload/products/product-14.jpg','ThinkPad X1 Carbon',9999.00,20,'电脑办公'),(15,'激光探测微尘，强劲吸力，多种吸头',_binary '\0','/upload/products/product-15.jpg','戴森吸尘器V15',4990.00,15,'家用电器'),(16,'除菌除甲醛，OLED显示屏，智能控制',_binary '\0','/upload/products/product-16.webp','小米空气净化器4',899.00,100,'家用电器'),(17,'4L大容量，智能预约，多功能菜单',_binary '\0','/upload/products/product-17.jpg','美的电饭煲',299.00,150,'家用电器'),(18,'500升对开门，风冷无霜，一级能效',_binary '\0','/upload/products/product-18.jpg','海尔冰箱BCD-500',3999.00,25,'家用电器'),(19,'30ml精华液，修护肌底，改善细纹',_binary '\0','/upload/products/product-19.jpg','兰蔻小黑瓶',1080.00,80,'美妆护肤'),(20,'50ml精华液，抗老修护，提亮肤色',_binary '\0','/upload/products/product-20.jpg','雅诗兰黛小棕瓶',880.00,90,'美妆护肤'),(21,'230ml精华液，改善肤质，提亮肌肤',_binary '\0','/upload/products/product-21.png','SK-II神仙水',1450.00,60,'美妆护肤'),(22,'60ml精华面霜，修护滋润，抗老紧致',_binary '\0','/upload/products/product-22.jpg','海蓝之谜面霜',2680.00,40,'美妆护肤'),(23,'经典篮球鞋，复古配色，头层牛皮',_binary '\0','/upload/products/product-23.jpg','耐克Air Jordan 1',1299.00,50,'运动户外'),(24,'专业跑步鞋，Boost中底，透气鞋面',_binary '\0','/upload/products/product-24.webp','阿迪达斯Ultra Boost',1199.00,70,'运动户外'),(25,'防水透气，专业登山，三合一设计',_binary '\0','/upload/products/product-25.webp','北面冲锋衣',1999.00,30,'运动户外'),(26,'2-3人自动帐篷，防风防雨，便携收纳',_binary '\0','/upload/products/product-26.webp','迪卡侬帐篷',399.00,25,'运动户外'),(27,'混合坚果，节日送礼，1680克装',_binary '\0','/upload/products/product-27.webp','三只松鼠坚果礼盒',168.00,200,'食品饮料'),(28,'550ml*24瓶，天然饮用水',_binary '\0','/upload/products/product-28.png','农夫山泉矿泉水',35.90,500,'食品饮料'),(29,'250克装，中度烘焙，阿拉比卡豆',_binary '\0','/upload/products/product-29.jpg','星巴克咖啡豆',108.00,80,'食品饮料'),(30,'榛果威化巧克力，30粒装礼盒',_binary '\0','/upload/products/product-30.jpg','费列罗巧克力',89.90,150,'食品饮料'),(31,'刘慈欣科幻小说，三册套装',_binary '\0','/upload/products/product-31.webp','三体全套',99.00,100,'图书文具'),(32,'经典硬皮横线本，口袋大小',_binary '\0','/upload/products/product-32.png','Moleskine笔记本',118.00,200,'图书文具'),(33,'FP-78G+系列，练字书法，F尖',_binary '\0','/upload/products/product-33.jpg','百乐钢笔',68.00,150,'图书文具'),(34,'12位大屏幕，商务办公，双电源',_binary '\0','/upload/products/product-34.webp','得力计算器',45.00,300,'图书文具'),(35,'可调节亮度，护眼学习，简约设计',_binary '\0','/upload/products/product-35.webp','宜家LED台灯',149.00,80,'家居生活'),(36,'超声波香薰加湿器，静音设计',_binary '\0','/upload/products/product-36.webp','无印良品香薰机',380.00,50,'家居生活'),(37,'全棉四件套，200x230cm，北欧风',_binary '\0','/upload/products/product-37.webp','Zara Home床品套装',599.00,30,'家居生活'),(38,'透明塑料，3件套，整理储物',_binary '\0','/upload/products/product-38.webp','宜家收纳盒',39.90,120,'家居生活'),(39,'纯棉圆领，男女同款，多色可选',_binary '\0','/upload/products/product-39.jpg','优衣库T恤',79.00,200,'服饰鞋包'),(40,'速干面料，弹性腰间，训练健身',_binary '\0','/upload/products/product-40.webp','Nike运动裤',299.00,100,'运动户外'),(41,'春季新品，印花设计，中长款',_binary '\0','/upload/products/product-41.jpg','Zara连衣裙',299.00,60,'服饰鞋包'),(42,'Boost缓震，网面透气，经典三条杠',_binary '\0','/upload/products/product-42.webp','阿迪达斯运动鞋',699.00,80,'运动户外'),(43,'这是一个测试用商品，请勿下单',_binary '','/upload/products/product-43.png','测试商品-请勿购买',999.99,999,'综合商品');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_activities`
--

DROP TABLE IF EXISTS `user_activities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_activities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_type` varchar(255) NOT NULL,
  `amount` double DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `duration_seconds` int DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `product_category` varchar(255) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `unit_price` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbe7yq8t74yxeoarmxlxevoped` (`user_id`),
  CONSTRAINT `FKbe7yq8t74yxeoarmxlxevoped` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_activities`
--

LOCK TABLES `user_activities` WRITE;
/*!40000 ALTER TABLE `user_activities` DISABLE KEYS */;
INSERT INTO `user_activities` VALUES (1,'ADD_TO_CART',NULL,'添加商品到购物车: 苹果耳机',NULL,3,'苹果耳机','2025-12-27 06:29:49.846342',1,NULL,NULL,NULL,NULL,NULL),(2,'PURCHASE_PRODUCT',1299,'购买商品: 苹果耳机, 订单号: 1',1,3,'苹果耳机','2025-12-27 06:31:22.127937',1,NULL,NULL,NULL,NULL,NULL),(3,'ADD_TO_CART',NULL,'添加商品到购物车: 大疆无人机',NULL,5,'大疆无人机','2025-12-27 06:32:32.174365',1,NULL,NULL,NULL,NULL,NULL),(4,'ADD_TO_CART',NULL,'添加商品到购物车: test-佳能',NULL,6,'test-佳能','2025-12-27 06:37:57.655527',1,NULL,NULL,NULL,NULL,NULL),(5,'PURCHASE_PRODUCT',1688,'购买商品: test-佳能, 订单号: 2',2,6,'test-佳能','2025-12-27 06:38:18.202094',1,NULL,NULL,NULL,NULL,NULL),(6,'ADD_TO_CART',NULL,'添加商品到购物车: 苹果耳机',NULL,3,'苹果耳机','2025-12-27 06:41:01.588888',1,NULL,NULL,NULL,NULL,NULL),(7,'ADD_TO_CART',NULL,'添加商品到购物车: 华为平板',NULL,2,'华为平板','2025-12-27 06:41:02.191129',1,NULL,NULL,NULL,NULL,NULL),(8,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2025-12-27 06:41:02.677298',1,NULL,NULL,NULL,NULL,NULL),(9,'PURCHASE_PRODUCT',2999,'购买商品: 小米手机, 订单号: 3',3,1,'小米手机','2025-12-27 06:41:12.617616',1,NULL,NULL,NULL,NULL,NULL),(10,'ADD_TO_CART',NULL,'添加商品到购物车: 华为平板',NULL,2,'华为平板','2025-12-27 06:41:15.713496',1,NULL,NULL,NULL,NULL,NULL),(11,'PURCHASE_PRODUCT',1899,'购买商品: 华为平板, 订单号: 4',4,2,'华为平板','2025-12-27 06:41:19.169939',1,NULL,NULL,NULL,NULL,NULL),(12,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2025-12-27 06:41:23.153657',1,NULL,NULL,NULL,NULL,NULL),(13,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2025-12-27 06:41:25.586687',1,NULL,NULL,NULL,NULL,NULL),(14,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2025-12-27 06:41:30.199582',1,NULL,NULL,NULL,NULL,NULL),(15,'PURCHASE_PRODUCT',2999,'购买商品: 小米手机, 订单号: 5',5,1,'小米手机','2025-12-27 06:41:39.379491',1,NULL,NULL,NULL,NULL,NULL),(16,'ADD_TO_CART',NULL,'添加商品到购物车: test-佳能',NULL,6,'test-佳能','2025-12-27 07:43:22.770467',1,NULL,NULL,NULL,NULL,NULL),(17,'PURCHASE_PRODUCT',1688,'购买商品: test-佳能, 订单号: 6',6,6,'test-佳能','2025-12-27 07:43:25.580211',1,NULL,NULL,NULL,NULL,NULL),(18,'ADD_TO_CART',NULL,'添加商品到购物车: test-佳能',NULL,6,'test-佳能','2025-12-27 07:47:04.139244',3,NULL,NULL,NULL,NULL,NULL),(19,'PURCHASE_PRODUCT',1688,'购买商品: test-佳能, 订单号: 7',7,6,'test-佳能','2025-12-27 07:47:19.399886',3,NULL,NULL,NULL,NULL,NULL),(20,'LOGIN',NULL,'用户登录: testuser',NULL,NULL,NULL,'2026-05-22 18:55:00.046321',1,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(21,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 18:55:00.120346',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(22,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 18:55:26.074542',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(23,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 18:55:32.346075',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(24,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 18:55:46.674848',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(25,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 未分类',NULL,NULL,NULL,'2026-05-22 18:55:47.437459',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(26,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 18:55:54.837103',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(27,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 未分类',NULL,NULL,NULL,'2026-05-22 18:55:55.851356',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(28,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 18:55:56.792181',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(29,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 未分类',NULL,NULL,NULL,'2026-05-22 19:12:19.521468',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(30,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 19:12:20.734441',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(31,'LOGIN',NULL,'用户登录: customer',NULL,NULL,NULL,'2026-05-22 19:56:21.210814',6,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(32,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 19:56:21.246937',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(33,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2026-05-22 19:56:24.455105',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,2999.00),(34,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 19:56:24.469338',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(35,'ADD_TO_CART',NULL,'添加商品到购物车: 联想笔记本',NULL,4,'联想笔记本','2026-05-22 19:56:26.815619',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,4999.00),(36,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 19:56:26.826873',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(37,'ADD_TO_CART',NULL,'添加商品到购物车: test-佳能',NULL,6,'test-佳能','2026-05-22 19:56:29.285948',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,1688.00),(38,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 19:56:29.296488',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(39,'ADD_TO_CART',NULL,'添加商品到购物车: 耐克Air Jordan 1',NULL,23,'耐克Air Jordan 1','2026-05-22 19:56:34.656588',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,1299.00),(40,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 19:56:34.668587',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(41,'ADD_TO_CART',NULL,'添加商品到购物车: 华为平板',NULL,2,'华为平板','2026-05-22 19:56:42.824330',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,1899.00),(42,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 19:56:42.836332',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(43,'PURCHASE_PRODUCT',2999,'购买商品: 小米手机, 订单号: 8',8,1,'小米手机','2026-05-22 19:57:37.423679',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,2999.00),(44,'PURCHASE_PRODUCT',1688,'购买商品: test-佳能, 订单号: 8',8,6,'test-佳能','2026-05-22 19:57:37.428293',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,1688.00),(45,'PURCHASE_PRODUCT',1299,'购买商品: 耐克Air Jordan 1, 订单号: 8',8,23,'耐克Air Jordan 1','2026-05-22 19:57:37.433293',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,1299.00),(46,'PURCHASE_PRODUCT',1899,'购买商品: 华为平板, 订单号: 8',8,2,'华为平板','2026-05-22 19:57:37.437293',6,NULL,'0:0:0:0:0:0:0:1','未分类',1,1899.00),(47,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 19:58:22.922339',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(48,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:04:07.853241',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(49,'BROWSE_PRODUCTS',NULL,'浏览商品列表，搜索: 小，全部分类',NULL,NULL,NULL,'2026-05-22 20:04:11.935526',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(50,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:04:54.293214',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(51,'LOGIN',NULL,'用户登录: customer',NULL,NULL,NULL,'2026-05-22 20:24:52.486546',6,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(52,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:24:52.524526',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(53,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:24:56.361344',6,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(54,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家居生活',NULL,NULL,NULL,'2026-05-22 20:24:56.648272',6,0,'0:0:0:0:0:0:0:1','家居生活',NULL,NULL),(55,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家用电器',NULL,NULL,NULL,'2026-05-22 20:24:57.048667',6,0,'0:0:0:0:0:0:0:1','家用电器',NULL,NULL),(56,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:24:57.426248',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(57,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:25:52.416740',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(58,'LOGIN',NULL,'用户登录: testuser',NULL,NULL,NULL,'2026-05-22 20:25:59.589434',1,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(59,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:25:59.611413',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(60,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:26:04.797206',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(61,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:26:06.055284',1,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(62,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:26:08.269987',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(63,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:26:12.207618',1,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(64,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:26:12.737944',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(65,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:26:13.299624',1,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(66,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家居生活',NULL,NULL,NULL,'2026-05-22 20:26:13.756614',1,0,'0:0:0:0:0:0:0:1','家居生活',NULL,NULL),(67,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:26:14.534689',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(68,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:26:16.049874',1,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(69,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家用电器',NULL,NULL,NULL,'2026-05-22 20:26:16.555800',1,0,'0:0:0:0:0:0:0:1','家用电器',NULL,NULL),(70,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家居生活',NULL,NULL,NULL,'2026-05-22 20:26:17.693321',1,0,'0:0:0:0:0:0:0:1','家居生活',NULL,NULL),(71,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:26:18.102044',1,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(72,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:26:19.609693',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(73,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:26:23.082186',1,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(74,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家居生活',NULL,NULL,NULL,'2026-05-22 20:26:24.554033',1,0,'0:0:0:0:0:0:0:1','家居生活',NULL,NULL),(75,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家用电器',NULL,NULL,NULL,'2026-05-22 20:26:26.527355',1,0,'0:0:0:0:0:0:0:1','家用电器',NULL,NULL),(76,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 手机数码',NULL,NULL,NULL,'2026-05-22 20:26:27.512598',1,0,'0:0:0:0:0:0:0:1','手机数码',NULL,NULL),(77,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 服饰鞋包',NULL,NULL,NULL,'2026-05-22 20:26:28.282714',1,0,'0:0:0:0:0:0:0:1','服饰鞋包',NULL,NULL),(78,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 电脑办公',NULL,NULL,NULL,'2026-05-22 20:26:29.004778',1,0,'0:0:0:0:0:0:0:1','电脑办公',NULL,NULL),(79,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:26:32.721911',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(80,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:28:07.136942',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(81,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:32:46.645586',1,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(82,'ADD_TO_CART',NULL,'添加商品到购物车: 百乐钢笔',NULL,33,'百乐钢笔','2026-05-22 20:32:49.406556',1,NULL,'0:0:0:0:0:0:0:1','图书文具',1,68.00),(83,'ADD_TO_CART',NULL,'添加商品到购物车: 苹果耳机',NULL,3,'苹果耳机','2026-05-22 20:32:51.039349',1,NULL,'0:0:0:0:0:0:0:1','手机数码',1,1299.00),(84,'LOGIN',NULL,'用户登录: customer',NULL,NULL,NULL,'2026-05-22 20:33:23.753738',6,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(85,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:33:23.788638',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(86,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:33:49.534749',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(87,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:33:53.019648',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(88,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:33:54.949471',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(89,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 服饰鞋包',NULL,NULL,NULL,'2026-05-22 20:34:00.063008',6,0,'0:0:0:0:0:0:0:1','服饰鞋包',NULL,NULL),(90,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家用电器',NULL,NULL,NULL,'2026-05-22 20:34:01.227208',6,0,'0:0:0:0:0:0:0:1','家用电器',NULL,NULL),(91,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 手机数码',NULL,NULL,NULL,'2026-05-22 20:34:01.809856',6,0,'0:0:0:0:0:0:0:1','手机数码',NULL,NULL),(92,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 电脑办公',NULL,NULL,NULL,'2026-05-22 20:34:02.670259',6,0,'0:0:0:0:0:0:0:1','电脑办公',NULL,NULL),(93,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:37:14.788597',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(94,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:46:07.815129',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(95,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 手机数码',NULL,NULL,NULL,'2026-05-22 20:46:10.616648',6,0,'0:0:0:0:0:0:0:1','手机数码',NULL,NULL),(96,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 服饰鞋包',NULL,NULL,NULL,'2026-05-22 20:46:11.194829',6,0,'0:0:0:0:0:0:0:1','服饰鞋包',NULL,NULL),(97,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 手机数码',NULL,NULL,NULL,'2026-05-22 20:46:11.754336',6,0,'0:0:0:0:0:0:0:1','手机数码',NULL,NULL),(98,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家居生活',NULL,NULL,NULL,'2026-05-22 20:46:12.064249',6,0,'0:0:0:0:0:0:0:1','家居生活',NULL,NULL),(99,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 综合商品',NULL,NULL,NULL,'2026-05-22 20:46:23.911115',6,0,'0:0:0:0:0:0:0:1','综合商品',NULL,NULL),(100,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 电脑办公',NULL,NULL,NULL,'2026-05-22 20:46:24.233229',6,0,'0:0:0:0:0:0:0:1','电脑办公',NULL,NULL),(101,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 美妆护肤',NULL,NULL,NULL,'2026-05-22 20:46:24.518372',6,0,'0:0:0:0:0:0:0:1','美妆护肤',NULL,NULL),(102,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 电脑办公',NULL,NULL,NULL,'2026-05-22 20:46:24.955343',6,0,'0:0:0:0:0:0:0:1','电脑办公',NULL,NULL),(103,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 手机数码',NULL,NULL,NULL,'2026-05-22 20:46:25.283611',6,0,'0:0:0:0:0:0:0:1','手机数码',NULL,NULL),(104,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 综合商品',NULL,NULL,NULL,'2026-05-22 20:46:25.990495',6,0,'0:0:0:0:0:0:0:1','综合商品',NULL,NULL),(105,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 运动户外',NULL,NULL,NULL,'2026-05-22 20:46:26.665329',6,0,'0:0:0:0:0:0:0:1','运动户外',NULL,NULL),(106,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 食品饮料',NULL,NULL,NULL,'2026-05-22 20:46:26.908558',6,0,'0:0:0:0:0:0:0:1','食品饮料',NULL,NULL),(107,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 美妆护肤',NULL,NULL,NULL,'2026-05-22 20:46:27.687206',6,0,'0:0:0:0:0:0:0:1','美妆护肤',NULL,NULL),(108,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 运动户外',NULL,NULL,NULL,'2026-05-22 20:46:28.344346',6,0,'0:0:0:0:0:0:0:1','运动户外',NULL,NULL),(109,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 综合商品',NULL,NULL,NULL,'2026-05-22 20:46:28.686536',6,0,'0:0:0:0:0:0:0:1','综合商品',NULL,NULL),(110,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 美妆护肤',NULL,NULL,NULL,'2026-05-22 20:46:29.311472',6,0,'0:0:0:0:0:0:0:1','美妆护肤',NULL,NULL),(111,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家用电器',NULL,NULL,NULL,'2026-05-22 20:46:32.579901',6,0,'0:0:0:0:0:0:0:1','家用电器',NULL,NULL),(112,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 手机数码',NULL,NULL,NULL,'2026-05-22 20:46:33.191103',6,0,'0:0:0:0:0:0:0:1','手机数码',NULL,NULL),(113,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家居生活',NULL,NULL,NULL,'2026-05-22 20:46:33.555412',6,0,'0:0:0:0:0:0:0:1','家居生活',NULL,NULL),(114,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:46:34.768525',6,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(115,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 20:46:35.102026',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(116,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 20:46:35.699888',6,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(117,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-22 21:18:27.028014',6,0,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(118,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-22 21:18:33.937249',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(119,'LOGIN',NULL,'用户登录: admin',NULL,NULL,NULL,'2026-05-23 13:35:03.495203',4,NULL,'127.0.0.1',NULL,NULL,NULL),(120,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 13:35:03.544515',4,0,'127.0.0.1','未分类',NULL,NULL),(121,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 13:35:28.628059',4,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(122,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 13:35:38.501765',4,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(123,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 13:35:45.646034',4,0,'127.0.0.1','未分类',NULL,NULL),(124,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:26:49.565419',4,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(125,'LOGIN',NULL,'用户登录: customer',NULL,NULL,NULL,'2026-05-23 14:27:04.391713',6,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(126,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:27:04.406900',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(127,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:27:14.335158',6,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(128,'LOGIN',NULL,'用户登录: admin',NULL,NULL,NULL,'2026-05-23 14:27:23.271438',4,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(129,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:27:25.984994',4,0,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(130,'LOGIN',NULL,'用户登录: admin',NULL,NULL,NULL,'2026-05-23 14:47:05.634411',4,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(131,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:47:16.809567',4,11,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(132,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:47:27.360179',4,2,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(133,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:47:29.063295',4,1,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(134,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:47:29.582001',4,1,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(135,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:47:29.873659',4,1,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(136,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:47:30.067236',4,1,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(137,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:47:30.246854',4,1,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(138,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-23 14:47:30.524469',4,6,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(139,'LOGIN',NULL,'用户登录: salesuser',NULL,NULL,NULL,'2026-05-23 14:48:09.664961',5,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(140,'LOGIN',NULL,'用户登录: customer',NULL,NULL,NULL,'2026-05-24 20:14:21.631921',6,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(141,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-24 20:14:21.680568',6,27,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(142,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-24 20:40:01.855301',6,107,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(143,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-24 20:57:11.970182',6,81,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(144,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-24 20:58:32.959260',6,691,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(145,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-24 21:10:03.774918',6,4,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(146,'LOGIN',NULL,'用户登录: customer',NULL,NULL,NULL,'2026-05-25 12:28:08.911705',6,NULL,'127.0.0.1',NULL,NULL,NULL),(147,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-25 12:28:08.967684',6,9,'127.0.0.1','未分类',NULL,NULL),(148,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家居生活',NULL,NULL,NULL,'2026-05-25 12:28:17.658379',6,1,'127.0.0.1','家居生活',NULL,NULL),(149,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 家用电器',NULL,NULL,NULL,'2026-05-25 12:28:18.330662',6,1,'0:0:0:0:0:0:0:1','家用电器',NULL,NULL),(150,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 手机数码',NULL,NULL,NULL,'2026-05-25 12:28:18.919166',6,1,'127.0.0.1','手机数码',NULL,NULL),(151,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 服饰鞋包',NULL,NULL,NULL,'2026-05-25 12:28:19.952036',6,1,'127.0.0.1','服饰鞋包',NULL,NULL),(152,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 电脑办公',NULL,NULL,NULL,'2026-05-25 12:28:20.389667',6,1,'127.0.0.1','电脑办公',NULL,NULL),(153,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 服饰鞋包',NULL,NULL,NULL,'2026-05-25 12:28:20.946223',6,2,'0:0:0:0:0:0:0:1','服饰鞋包',NULL,NULL),(154,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，分类: 图书文具',NULL,NULL,NULL,'2026-05-25 12:28:23.061139',6,4,'0:0:0:0:0:0:0:1','图书文具',NULL,NULL),(155,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-25 12:28:27.206802',6,9,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(156,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-25 12:58:55.989445',6,2,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(157,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-25 13:15:11.589533',6,1,'0:0:0:0:0:0:0:1','未分类',NULL,NULL),(158,'LOGIN',NULL,'用户登录: testuser',NULL,NULL,NULL,'2026-05-25 13:25:02.637777',1,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(159,'LOGIN',NULL,'用户登录: codexuser1779686718294',NULL,NULL,NULL,'2026-05-25 13:25:34.467616',7,NULL,'0:0:0:0:0:0:0:1',NULL,NULL,NULL),(160,'BROWSE_PRODUCTS',NULL,'浏览商品列表，全部商品，全部分类',NULL,NULL,NULL,'2026-05-25 13:25:34.500778',7,16,'0:0:0:0:0:0:0:1','未分类',NULL,NULL);
/*!40000 ALTER TABLE `user_activities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` decimal(38,2) NOT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,17001.00,'testuser@qq.com','测试用户','$2a$10$1x6/xDsp6QVgTV6iFzJY5OdgJ7iLspiT2.6uBUIJV2E2WCsfnShAW','ADMIN','testuser'),(3,8301.00,'1486234558@qq.com','Layyar','$2a$10$XakYUp.NwBwqhkGi3hgT/OTdF9Q6L8Z9hGzhUHReb6UdGbb9TG6nO','USER','Layyar'),(4,99999999.00,'admin@example.com','系统管理员','$2a$10$1x6/xDsp6QVgTV6iFzJY5OdgJ7iLspiT2.6uBUIJV2E2WCsfnShAW','ADMIN','admin'),(5,999999.00,'salesuser@example.com','销售人员','$2a$10$1x6/xDsp6QVgTV6iFzJY5OdgJ7iLspiT2.6uBUIJV2E2WCsfnShAW','SALES','salesuser'),(6,2115.00,'customer@example.com','普通用户','$2a$10$1x6/xDsp6QVgTV6iFzJY5OdgJ7iLspiT2.6uBUIJV2E2WCsfnShAW','USER','customer'),(7,0.00,'codexuser1779686718294@example.com',NULL,'$2a$10$Wx.ZTouu8EZn6s85w0imVOOWkfNXta6g4PHYXkC.PYxRknE7PWgKO','USER','codexuser1779686718294');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'ecommerce_db'
--

--
-- Dumping routines for database 'ecommerce_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-25 10:02:58

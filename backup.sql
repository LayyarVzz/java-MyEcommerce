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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,'SCUT','testUser','2025-12-27 06:31:03.546015',_binary '','190xxx',1),(2,'SCUT','layyar','2025-12-27 07:47:15.255307',_binary '','190xxx',3),(3,'SCUT 2','testUser1','2025-12-27 08:57:24.558351',_binary '\0','190xxx',1),(4,'SCUT 3','testUser2','2025-12-27 08:57:43.104809',_binary '\0','190xxx',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1299.00,1,1,3),(2,1688.00,1,2,6),(3,2999.00,1,3,1),(4,1899.00,1,4,2),(5,2999.00,1,5,1),(6,1688.00,1,6,6),(7,1688.00,1,7,6);
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'testUser','190xxx','SCUT','2025-12-27 06:31:22.087748','已取消',1299.00,1),(2,'testUser','190xxx','SCUT','2025-12-27 06:38:18.171116','已取消',1688.00,1),(3,'testUser','190xxx','SCUT','2025-12-27 06:41:12.594301','已确认',2999.00,1),(4,'testUser','190xxx','SCUT','2025-12-27 06:41:19.144459','已确认',1899.00,1),(5,'testUser','190xxx','SCUT','2025-12-27 06:41:39.355670','待处理',2999.00,1),(6,'testUser','190xxx','SCUT','2025-12-27 07:43:25.548673','待处理',1688.00,1),(7,'layyar','190xxx','SCUT','2025-12-27 07:47:19.374036','待处理',1688.00,3);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'高性能智能手机，8GB+256GB',_binary '\0','https://d1eh9yux7w8iql.cloudfront.net/product_images/379857_b78107b8-0f6f-467f-9318-b2b5e8a24481.jpg','小米手机',2999.00,99),(2,'10.4英寸大屏，学习办公神器',_binary '\0','https://th.bing.com/th/id/OIP.c7QmH47GIF-VR7pc4h0aXgHaGR?w=234&h=199&c=7&r=0&o=7&dpr=1.5&pid=1.7&rm=3','华为平板',1899.00,47),(3,'无线蓝牙耳机，降噪功能',_binary '\0','https://th.bing.com/th/id/OIP.rL5w1XV4trp35nEI7bSquAHaFc?w=247&h=182&c=7&r=0&o=7&dpr=1.5&pid=1.7&rm=3','苹果耳机',1299.00,200),(4,'轻薄本，16GB+512GB',_binary '\0','https://p3.lefile.cn/fes/cms/2022/10/25/fgi4faziysgywapttpudt6uiv7vc7t347905.jpg','联想笔记本',4999.00,30),(5,'高清航拍，便携折叠',_binary '\0','https://zhongces3.sina.com.cn/products/201802/672728e4e34454a5197aee89747a6a08.png','大疆无人机',3699.00,20),(6,'测试商品，请勿购买！',_binary '\0','https://img.pconline.com.cn/images/upload/upc/tx/onlinephotolib/2008/06/c0/224290590_1596679013124.jpg','test-佳能',1688.00,88),(7,'钛金属设计，A17 Pro芯片，专业级摄像系统',_binary '\0','https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/iphone-15-pro-finish-select-202309-6-1inch?wid=2560&hei=1440&fmt=jpeg&qlt=90&.v=1693013840572','iPhone 15 Pro',7999.00,50),(8,'AI智能手机，5000万像素，超视觉夜拍',_binary '\0','https://imgservice.suning.cn/uimg1/b2c/image/d39opMz4qxe4mvM9Gz5WWQ.png','三星Galaxy S24',5999.00,80),(9,'哈苏影像，超光影三主摄，100W超级闪充',_binary '\0','https://img1.mydrivers.com/img/20240108/3a7487a6cd9c41f0b1653731602960a5.jpg','OPPO Find X7',4499.00,60),(10,'蔡司影像，天玑9300旗舰芯片，120W双芯闪充',_binary '\0','https://2c.zol-img.com.cn/product/256/914/ceoLiaHOiKP4o.jpg','vivo X100',4299.00,70),(11,'13.6英寸，8GB+256GB，午夜色',_binary '\0','https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/mba13-midnight-select-202402?wid=904&hei=840&fmt=jpeg&qlt=90&.v=1708367688034','MacBook Air M2',8999.00,25),(12,'13.4英寸轻薄本，Intel i7处理器，16GB+512GB',_binary '\0','https://tse3-mm.cn.bing.net/th/id/OIP-C._e5lrOnRSa7qinF1ktDUKwHaHa?w=166&h=180&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','戴尔XPS 13',6999.00,30),(13,'14英寸触控屏，12代酷睿，16GB+512GB',_binary '\0','https://tse3-mm.cn.bing.net/th/id/OIP-C.WfPY8sfcyqrV5JYixcxTqQHaGV?w=195&h=180&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','华为MateBook 14',5499.00,40),(14,'14英寸商务本，碳纤维机身，16GB+1TB',_binary '\0','https://p4.lefile.cn/product/adminweb/2025/04/28/HaG2iPB7aTGsiQ3nRtjj8N4Wx-4152.jpg','ThinkPad X1 Carbon',9999.00,20),(15,'激光探测微尘，强劲吸力，多种吸头',_binary '\0','https://imgservice.suning.cn/uimg1/b2c/image/vErLR36SZ4sgphqTd1sJlQ.jpg_800w_800h_4e','戴森吸尘器V15',4990.00,15),(16,'除菌除甲醛，OLED显示屏，智能控制',_binary '\0','https://i01.appmifile.com/v1/MI_18455B3E4DA706226CF7535A58E875F0267/pms_1635490144.37164386.png?thumb=1&f=webp&q=85','小米空气净化器4',899.00,100),(17,'4L大容量，智能预约，多功能菜单',_binary '\0','https://dsdcp.smartmidea.net/mcsp/prod/20210422/1619032810609.jpg','美的电饭煲',299.00,150),(18,'500升对开门，风冷无霜，一级能效',_binary '\0','https://imgservice.suning.cn/uimg1/b2c/image/6Rhb2MQsNXBiqk2nJnSZhg.jpg_800w_800h_4e','海尔冰箱BCD-500',3999.00,25),(19,'30ml精华液，修护肌底，改善细纹',_binary '\0','https://imgservice.suning.cn/uimg1/b2c/image/i3bzH48zXNaMRCnWzeYQpg.jpg','兰蔻小黑瓶',1080.00,80),(20,'50ml精华液，抗老修护，提亮肤色',_binary '\0','https://cdn.55haitao.com/bbs/data/attachment/deal/2020/11/29/1364255c8c77aec54b6761fefc1e58d6334e.jpg@!bbsc1','雅诗兰黛小棕瓶',880.00,90),(21,'230ml精华液，改善肤质，提亮肌肤',_binary '\0','https://www.anyserves.shop/fileserver/image/group1/M00/21/54/rBAAcV9rPuWACDhaAAMuVokNmvw212.jpg','SK-II神仙水',1450.00,60),(22,'60ml精华面霜，修护滋润，抗老紧致',_binary '\0','https://tse3-mm.cn.bing.net/th/id/OIP-C.aVIORkzgw-PiqWB6dMMD5wHaHa?o=7rm=3&rs=1&pid=ImgDetMain&o=7&rm=3','海蓝之谜面霜',2680.00,40),(23,'经典篮球鞋，复古配色，头层牛皮',_binary '\0','https://ts1.tc.mm.bing.net/th/id/R-C.6f2726c967b58295cc5bdb9019e17bbf?rik=NOZ%2fiLEs5pk2RA&riu=http%3a%2f%2fwww2.flightclub.cn%2fnews%2fuploads%2fallimg%2f190719%2f12-1ZG9103I7-52.jpg&ehk=f2kYOcJtbfKtFi1tJPSJxs5ySt6RDwsks8SMZCWIVkA%3d&risl=&pid=ImgRaw&r=0','耐克Air Jordan 1',1299.00,50),(24,'专业跑步鞋，Boost中底，透气鞋面',_binary '\0','https://ts2.tc.mm.bing.net/th/id/OIP-C.wwD-NAXWMQtwszTj-BXobwHaEc?rs=1&pid=ImgDetMain&o=7&rm=3','阿迪达斯Ultra Boost',1199.00,70),(25,'防水透气，专业登山，三合一设计',_binary '\0','https://tse3-mm.cn.bing.net/th/id/OIP-C.cuBA1y0JAVPrEtZLSlfF_gHaHa?w=193&h=194&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','北面冲锋衣',1999.00,30),(26,'2-3人自动帐篷，防风防雨，便携收纳',_binary '\0','https://ts4.tc.mm.bing.net/th/id/OIP-C.OiK0BHZTKksBXfyLOnwvigHaHa?rs=1&pid=ImgDetMain&o=7&rm=3','迪卡侬帐篷',399.00,25),(27,'混合坚果，节日送礼，1680克装',_binary '\0','https://tse2-mm.cn.bing.net/th/id/OIP-C.kyndABqlvU4fpck6googWQHaHa?w=199&h=199&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','三只松鼠坚果礼盒',168.00,200),(28,'550ml*24瓶，天然饮用水',_binary '\0','https://image.hinew.com.cn/shop/store/goods/211/211_1556180663043048595.png','农夫山泉矿泉水',35.90,500),(29,'250克装，中度烘焙，阿拉比卡豆',_binary '\0','https://img30.360buyimg.com/popWaterMark/jfs/t10960/253/379195384/375291/9002a87a/59cda273N7d9e26bb.jpg','星巴克咖啡豆',108.00,80),(30,'榛果威化巧克力，30粒装礼盒',_binary '\0','https://image.pp918.com/BrandNews/20201104/20201104113046_0341.jpg','费列罗巧克力',89.90,150),(31,'刘慈欣科幻小说，三册套装',_binary '\0','https://ts2.tc.mm.bing.net/th/id/OIP-C.X6XccZdm1XoLfl-wOhRmQgHaHa?rs=1&pid=ImgDetMain&o=7&rm=3','三体全套',99.00,100),(32,'经典硬皮横线本，口袋大小',_binary '\0','https://tse4-mm.cn.bing.net/th/id/OIP-C.hguvzssI19p_5U7ngpjeEQHaHa?w=198&h=198&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','Moleskine笔记本',118.00,200),(33,'FP-78G+系列，练字书法，F尖',_binary '\0','https://img14.360buyimg.com/pop/s500x500_jfs/t1/174291/16/41750/127587/6503d655Fea27db7a/9137b6b3608350cf.jpg','百乐钢笔',68.00,150),(34,'12位大屏幕，商务办公，双电源',_binary '\0','https://tse4-mm.cn.bing.net/th/id/OIP-C.Tq4Q0nt3u_RXATTMRbfruwHaHa?w=192&h=192&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','得力计算器',45.00,300),(35,'可调节亮度，护眼学习，简约设计',_binary '\0','https://tse4-mm.cn.bing.net/th/id/OIP-C.fZeTEylxLGSoQZ3Q_K7vhwHaHa?w=191&h=191&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','宜家LED台灯',149.00,80),(36,'超声波香薰加湿器，静音设计',_binary '\0','https://tse4-mm.cn.bing.net/th/id/OIP-C.pg_jxPR8HygSCg4YQjWSWQHaHa?w=160&h=180&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','无印良品香薰机',380.00,50),(37,'全棉四件套，200x230cm，北欧风',_binary '\0','https://tse1-mm.cn.bing.net/th/id/OIP-C.bm22OZcT3-DOAf1C7e9ilAHaHa?w=192&h=192&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','Zara Home床品套装',599.00,30),(38,'透明塑料，3件套，整理储物',_binary '\0','https://tse4-mm.cn.bing.net/th/id/OIP-C.RZvlt8uOWKQZXFUQHQCIQwHaEK?w=261&h=180&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','宜家收纳盒',39.90,120),(39,'纯棉圆领，男女同款，多色可选',_binary '\0','https://www.uniqlo.cn/public/image/L3/spl-contents/collaboration/Masterpiece/24SS/assets/imgs/subpage/tshirt/MPT04/relatedProduct_04_01.jpg','优衣库T恤',79.00,200),(40,'速干面料，弹性腰间，训练健身',_binary '\0','https://tse3-mm.cn.bing.net/th/id/OIP-C.E5krdzvYg8QLMnhmakrTcgHaHa?w=184&h=184&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','Nike运动裤',299.00,100),(41,'春季新品，印花设计，中长款',_binary '\0','https://tse2-mm.cn.bing.net/th/id/OIP-C.AuRMAfJG59wpefvV6QlQOgAAAA?w=176&h=176&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','Zara连衣裙',299.00,60),(42,'Boost缓震，网面透气，经典三条杠',_binary '\0','https://tse1-mm.cn.bing.net/th/id/OIP-C.KNGO9_qDr-w01vTf1MfEuQHaHa?w=214&h=214&c=7&r=0&o=7&dpr=1.8&pid=1.7&rm=3','阿迪达斯运动鞋',699.00,80),(43,'这是一个测试用商品，请勿下单',_binary '','https://via.placeholder.com/300x300.png?text=TEST+PRODUCT','测试商品-请勿购买',999.99,999);
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
  PRIMARY KEY (`id`),
  KEY `FKbe7yq8t74yxeoarmxlxevoped` (`user_id`),
  CONSTRAINT `FKbe7yq8t74yxeoarmxlxevoped` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_activities`
--

LOCK TABLES `user_activities` WRITE;
/*!40000 ALTER TABLE `user_activities` DISABLE KEYS */;
INSERT INTO `user_activities` VALUES (1,'ADD_TO_CART',NULL,'添加商品到购物车: 苹果耳机',NULL,3,'苹果耳机','2025-12-27 06:29:49.846342',1),(2,'PURCHASE_PRODUCT',1299,'购买商品: 苹果耳机, 订单号: 1',1,3,'苹果耳机','2025-12-27 06:31:22.127937',1),(3,'ADD_TO_CART',NULL,'添加商品到购物车: 大疆无人机',NULL,5,'大疆无人机','2025-12-27 06:32:32.174365',1),(4,'ADD_TO_CART',NULL,'添加商品到购物车: test-佳能',NULL,6,'test-佳能','2025-12-27 06:37:57.655527',1),(5,'PURCHASE_PRODUCT',1688,'购买商品: test-佳能, 订单号: 2',2,6,'test-佳能','2025-12-27 06:38:18.202094',1),(6,'ADD_TO_CART',NULL,'添加商品到购物车: 苹果耳机',NULL,3,'苹果耳机','2025-12-27 06:41:01.588888',1),(7,'ADD_TO_CART',NULL,'添加商品到购物车: 华为平板',NULL,2,'华为平板','2025-12-27 06:41:02.191129',1),(8,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2025-12-27 06:41:02.677298',1),(9,'PURCHASE_PRODUCT',2999,'购买商品: 小米手机, 订单号: 3',3,1,'小米手机','2025-12-27 06:41:12.617616',1),(10,'ADD_TO_CART',NULL,'添加商品到购物车: 华为平板',NULL,2,'华为平板','2025-12-27 06:41:15.713496',1),(11,'PURCHASE_PRODUCT',1899,'购买商品: 华为平板, 订单号: 4',4,2,'华为平板','2025-12-27 06:41:19.169939',1),(12,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2025-12-27 06:41:23.153657',1),(13,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2025-12-27 06:41:25.586687',1),(14,'ADD_TO_CART',NULL,'添加商品到购物车: 小米手机',NULL,1,'小米手机','2025-12-27 06:41:30.199582',1),(15,'PURCHASE_PRODUCT',2999,'购买商品: 小米手机, 订单号: 5',5,1,'小米手机','2025-12-27 06:41:39.379491',1),(16,'ADD_TO_CART',NULL,'添加商品到购物车: test-佳能',NULL,6,'test-佳能','2025-12-27 07:43:22.770467',1),(17,'PURCHASE_PRODUCT',1688,'购买商品: test-佳能, 订单号: 6',6,6,'test-佳能','2025-12-27 07:43:25.580211',1),(18,'ADD_TO_CART',NULL,'添加商品到购物车: test-佳能',NULL,6,'test-佳能','2025-12-27 07:47:04.139244',3),(19,'PURCHASE_PRODUCT',1688,'购买商品: test-佳能, 订单号: 7',7,6,'test-佳能','2025-12-27 07:47:19.399886',3);
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,12314.00,'1486234558@qq.com',NULL,'$2a$10$wLN5y5RxZApDlCuKIns2OOqz4X3YhqARQ9m7TEwR3AgqhpmrUvDNm','ADMIN','testuser'),(3,8301.00,'202330450691@mail.scut.edu.cn',NULL,'$2a$10$XakYUp.NwBwqhkGi3hgT/OTdF9Q6L8Z9hGzhUHReb6UdGbb9TG6nO','USER','Layyar');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-22 10:50:21

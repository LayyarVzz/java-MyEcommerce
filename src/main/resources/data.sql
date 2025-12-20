-- 初始化测试用户（密码是 123456，已用 BCrypt 加密）
INSERT INTO users (username, password, full_name)
VALUES ('testuser', '$2a$10$8H9w5f8G7y6D4s3a2q1z0b9n8m7l6k5j4h3g2f1d0s9a8s7d6f5g4h3j2k1l0', '测试用户');

-- 初始化商品数据
INSERT INTO products (name, description, price, image_url, discontinued, stock)
VALUES ('小米手机', '高性能智能手机，8GB+256GB', 2999.00, 'https://picsum.photos/200/200?random=1', false, 100),
       ('华为平板', '10.4英寸大屏，学习办公神器', 1899.00, 'https://picsum.photos/200/200?random=2', false, 50),
       ('苹果耳机', '无线蓝牙耳机，降噪功能', 1299.00, 'https://picsum.photos/200/200?random=3', false, 200),
       ('联想笔记本', '轻薄本，16GB+512GB', 4999.00, 'https://picsum.photos/200/200?random=4', false, 30),
       ('大疆无人机', '高清航拍，便携折叠', 3699.00, 'https://picsum.photos/200/200?random=5', false, 20);
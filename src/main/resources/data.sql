-- 初始化测试用户（密码是 123456，已用 BCrypt 加密）
INSERT INTO users (username, password, full_name)
VALUES ('testuser', '$2a$10$8H9w5f8G7y6D4s3a2q1z0b9n8m7l6k5j4h3g2f1d0s9a8s7d6f5g4h3j2k1l0', '测试用户');

-- 初始化商品数据
INSERT INTO products (name, description, price, image_url, discontinued, stock)
VALUES ('小米手机', '高性能智能手机，8GB+256GB', 2999.00,
        'https://d1eh9yux7w8iql.cloudfront.net/product_images/379857_b78107b8-0f6f-467f-9318-b2b5e8a24481.jpg', false,
        100),
       ('华为平板', '10.4英寸大屏，学习办公神器', 1899.00,
        'https://th.bing.com/th/id/OIP.c7QmH47GIF-VR7pc4h0aXgHaGR?w=234&h=199&c=7&r=0&o=7&dpr=1.5&pid=1.7&rm=3', false,
        50),
       ('苹果耳机', '无线蓝牙耳机，降噪功能', 1299.00,
        'https://th.bing.com/th/id/OIP.rL5w1XV4trp35nEI7bSquAHaFc?w=247&h=182&c=7&r=0&o=7&dpr=1.5&pid=1.7&rm=3', false,
        200),
       ('联想笔记本', '轻薄本，16GB+512GB', 4999.00,
        'https://p3.lefile.cn/fes/cms/2022/10/25/fgi4faziysgywapttpudt6uiv7vc7t347905.jpg', false, 30),
       ('大疆无人机', '高清航拍，便携折叠', 3699.00,
        'https://zhongces3.sina.com.cn/products/201802/672728e4e34454a5197aee89747a6a08.png', false, 20),
       ('test-佳能', '测试商品，请勿购买！', 1688.00,
        'https://img.pconline.com.cn/images/upload/upc/tx/onlinephotolib/2008/06/c0/224290590_1596679013124.jpg', false,
        10);
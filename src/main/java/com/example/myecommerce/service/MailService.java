package com.example.myecommerce.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MailService {
    // 获取yml配置的发送者邮箱
    @Value("${spring.mail.username}")
    private String mainUserName;

    //发送人昵称
    @Value("${spring.mail.nickname}")
    private String nickname;

    // 邮件发送者
    private final JavaMailSender mailSender;

    public void sendOrderConfirm(String toEmail, String orderNo, BigDecimal amount) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            // 发送人昵称
            helper.setFrom("MyEcommerce <" + mainUserName + ">");

            // 接收人
            helper.setTo(toEmail);

            // 主题
            helper.setSubject("订单确认 – 您的订单 " + orderNo + " 已支付成功");

            // 内容
            String html = """
                    <div style="font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#333;">
                      <h2 style="color:#0066cc;">订单确认</h2>
                      <p>尊敬的顾客，您好！</p>
                      <p>您的订单已确认，详细信息如下：</p>
                      <ul>
                        <li><strong>订单号：</strong>%s</li>
                        <li><strong>订单金额：</strong>¥%s</li>
                      </ul>
                      <p>我们将尽快为您安排发货，请注意查收。</p>
                      <p>如有疑问，请直接回复本邮件。</p>
                      <p>感谢您的光临！</p>
                    </div>
                    """.formatted(orderNo, amount);
            helper.setText(html, true);   // true = 是 HTML

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("订单确认邮件发送失败", e);
        }
    }
}


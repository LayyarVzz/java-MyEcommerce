package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.service.AddressService;
import com.example.myecommerce.service.CartService;
import com.example.myecommerce.service.MailService;
import com.example.myecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private AddressService addressService;

    @Mock
    private UserService userService;

    @Mock
    private MailService mailService;

    private CartController controller;

    @BeforeEach
    void setUp() {
        controller = new CartController(cartService, addressService, userService, mailService);
    }

    @Test
    void updateQuantityDelegatesToCartServiceForAuthenticatedUser() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.updateQuantity(45L, 3, authentication(), redirectAttributes);

        assertThat(view).isEqualTo("redirect:/cart");
        verify(cartService).updateCartItemQuantity("customer", 45L, 3);
    }

    @Test
    void checkoutSendsConfirmationToSelectedAddressEmailSnapshot() {
        Order order = new Order();
        order.setId(88L);
        order.setTotalAmount(new BigDecimal("128.50"));
        order.setContactEmail("receiver@example.com");

        when(cartService.hasSufficientBalance("customer")).thenReturn(true);
        when(cartService.createOrderFromCart("customer", 7L, "127.0.0.1")).thenReturn(order);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        controller.checkout(7L, authentication(), redirectAttributes, request());

        verify(mailService).sendOrderConfirm("receiver@example.com", "ORD-88", new BigDecimal("128.50"));
    }

    @Test
    void checkoutCompletesOrderWhenConfirmationEmailFails() {
        Order order = new Order();
        order.setId(88L);
        order.setTotalAmount(new BigDecimal("128.50"));
        order.setContactEmail("receiver@example.com");

        when(cartService.hasSufficientBalance("customer")).thenReturn(true);
        when(cartService.createOrderFromCart("customer", 7L, "127.0.0.1")).thenReturn(order);
        doThrow(new RuntimeException("SMTP connection failed"))
                .when(mailService).sendOrderConfirm("receiver@example.com", "ORD-88", new BigDecimal("128.50"));

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String result = controller.checkout(7L, authentication(), redirectAttributes, request());

        assertThat(result).isEqualTo("redirect:/orders/88?checkout=success");
        assertThat(redirectAttributes.getFlashAttributes().get("success"))
                .isEqualTo("购买成功！商品将发送至您的收货地址。");
        assertThat(redirectAttributes.getFlashAttributes().get("warning"))
                .isEqualTo("订单已创建，但确认邮件发送失败。请检查邮件配置或网络后稍后重试。");
        verify(cartService).clearCart("customer");
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("customer", "password", List.of());
    }

    private MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        return request;
    }
}

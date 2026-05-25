package com.example.myecommerce.service;

import com.example.myecommerce.entity.Address;
import com.example.myecommerce.entity.CartItem;
import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.repository.CartItemRepository;
import com.example.myecommerce.repository.OrderItemRepository;
import com.example.myecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private UserActivityService userActivityService;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(
                cartItemRepository,
                productService,
                userService,
                orderRepository,
                orderItemRepository,
                addressService,
                userActivityService
        );
    }

    @Test
    void updateCartItemQuantitySetsQuantityForCurrentUserItem() {
        User user = user();
        CartItem item = cartItem(user, product(), 1);
        item.setId(21L);

        when(userService.getCurrentUser("customer")).thenReturn(user);
        when(cartItemRepository.findById(21L)).thenReturn(Optional.of(item));

        cartService.updateCartItemQuantity("customer", 21L, 3);

        assertThat(item.getQuantity()).isEqualTo(3);
        verify(cartItemRepository).save(item);
    }

    @Test
    void updateCartItemQuantityRejectsZeroQuantity() {
        assertThatThrownBy(() -> cartService.updateCartItemQuantity("customer", 21L, 0))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("商品数量必须至少为 1");

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void updateCartItemQuantityRejectsAnotherUsersItem() {
        User user = user();
        User otherUser = user();
        otherUser.setId(2L);
        otherUser.setUsername("other");
        CartItem item = cartItem(otherUser, product(), 2);
        item.setId(22L);

        when(userService.getCurrentUser("customer")).thenReturn(user);
        when(cartItemRepository.findById(22L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> cartService.updateCartItemQuantity("customer", 22L, 4))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("购物车项不存在");

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void createOrderCopiesRecipientEmailFromSelectedAddress() {
        User user = user();
        Product product = product();
        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(2);
        Address address = address(user);

        when(userService.getCurrentUser("customer")).thenReturn(user);
        when(cartItemRepository.findByUser(user)).thenReturn(List.of(item));
        when(addressService.getAddressById(7L)).thenReturn(address);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(99L);
            return order;
        });

        Order order = cartService.createOrderFromCart("customer", 7L, "127.0.0.1");

        assertThat(order.getContactEmail()).isEqualTo("receiver@example.com");
        assertThat(order.getContactName()).isEqualTo("张三");
        assertThat(order.getContactPhone()).isEqualTo("13800000000");
        assertThat(order.getDeliveryAddress()).isEqualTo("上海市浦东新区");
    }

    private CartItem cartItem(User user, Product product, int quantity) {
        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }

    private User user() {
        User user = new User();
        user.setId(1L);
        user.setUsername("customer");
        user.setEmail("account@example.com");
        user.setBalance(new BigDecimal("100.00"));
        return user;
    }

    private Product product() {
        Product product = new Product();
        product.setId(11L);
        product.setName("台灯");
        product.setPrice(new BigDecimal("10.00"));
        product.setStock(10);
        return product;
    }

    private Address address(User user) {
        Address address = new Address();
        address.setId(7L);
        address.setUser(user);
        address.setContactName("张三");
        address.setPhone("13800000000");
        address.setAddress("上海市浦东新区");
        address.setEmail("receiver@example.com");
        return address;
    }
}

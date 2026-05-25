package com.example.myecommerce.controller;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.AddressService;
import com.example.myecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentCaptor.forClass;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @Mock
    private UserService userService;

    private AddressController controller;

    @BeforeEach
    void setUp() {
        controller = new AddressController(addressService, userService);
    }

    @Test
    void addressListKeepsEmailOnEachAddressInsteadOfAccountLevelEmail() {
        User user = user("customer", "buyer@example.com");
        when(userService.getCurrentUser("customer")).thenReturn(user);
        when(addressService.getUserAddresses("customer")).thenReturn(List.of());
        when(addressService.canAddMoreAddresses("customer")).thenReturn(true);

        Model model = new ExtendedModelMap();
        String view = controller.listAddresses(model, authentication());

        assertThat(view).isEqualTo("address-list");
        assertThat(model.asMap()).doesNotContainKey("accountEmail");
    }

    @Test
    void addAddressSavesRecipientEmailOnAddress() {
        when(addressService.canAddMoreAddresses("customer")).thenReturn(true);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirect = controller.addAddress(
                "customer",
                "13800000000",
                "上海市浦东新区",
                "  receiver@example.com  ",
                true,
                authentication(),
                redirectAttributes
        );

        assertThat(redirect).isEqualTo("redirect:/addresses");
        ArgumentCaptor<com.example.myecommerce.entity.Address> captor = forClass(com.example.myecommerce.entity.Address.class);
        verify(addressService).saveAddress(eq("customer"), captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("receiver@example.com");
    }

    @Test
    void editAddressUpdatesRecipientEmailOnAddress() {
        User user = user("customer", "buyer@example.com");
        com.example.myecommerce.entity.Address existingAddress = new com.example.myecommerce.entity.Address();
        existingAddress.setId(7L);
        existingAddress.setUser(user);
        existingAddress.setEmail("old@example.com");
        when(addressService.getAddressById(7L)).thenReturn(existingAddress);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirect = controller.editAddress(
                7L,
                "customer",
                "13800000000",
                "上海市浦东新区",
                "new-receiver@example.com",
                false,
                authentication(),
                redirectAttributes
        );

        assertThat(redirect).isEqualTo("redirect:/addresses");
        verify(addressService).updateAddress("customer", existingAddress);
        assertThat(existingAddress.getEmail()).isEqualTo("new-receiver@example.com");
    }

    @Test
    void addAddressRejectsIncompleteRecipientEmail() {
        when(addressService.canAddMoreAddresses("customer")).thenReturn(true);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirect = controller.addAddress(
                "customer",
                "13800000000",
                "上海市浦东新区",
                "buyer@",
                true,
                authentication(),
                redirectAttributes
        );

        assertThat(redirect).isEqualTo("redirect:/addresses/add");
        verify(addressService, never()).saveAddress(any(), any());
        assertThat(redirectAttributes.getFlashAttributes().get("addressError")).isEqualTo("请输入有效的收件邮箱。");
    }

    private User user(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setBalance(BigDecimal.ZERO);
        return user;
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("customer", "password", List.of());
    }
}

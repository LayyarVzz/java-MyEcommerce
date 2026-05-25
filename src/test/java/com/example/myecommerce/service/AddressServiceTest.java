package com.example.myecommerce.service;

import com.example.myecommerce.entity.Address;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserService userService;

    private AddressService addressService;

    @BeforeEach
    void setUp() {
        addressService = new AddressService(addressRepository, userService);
    }

    @Test
    void updateAddressSettingDefaultAlwaysClearsOtherDefaultAddresses() {
        User user = user();
        Address address = address(user, 7L, true);

        when(userService.getCurrentUser("customer")).thenReturn(user);
        when(addressRepository.findById(7L)).thenReturn(Optional.of(address));

        addressService.updateAddress("customer", address);

        verify(addressRepository).unsetDefaultAddresses(user);
        verify(addressRepository).save(address);
    }

    @Test
    void getUserAddressesNormalizesMultipleDefaultAddresses() {
        User user = user();
        Address earliestDefault = address(user, 7L, true);
        earliestDefault.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        Address duplicateDefault = address(user, 8L, true);
        duplicateDefault.setCreatedAt(LocalDateTime.of(2026, 1, 2, 10, 0));
        Address normalAddress = address(user, 9L, false);
        normalAddress.setCreatedAt(LocalDateTime.of(2026, 1, 3, 10, 0));

        when(userService.getCurrentUser("customer")).thenReturn(user);
        when(addressRepository.findByUser(user)).thenReturn(List.of(earliestDefault, duplicateDefault, normalAddress));

        addressService.getUserAddresses("customer");

        verify(addressRepository).save(duplicateDefault);
        assertThat(earliestDefault.getIsDefault()).isTrue();
        assertThat(duplicateDefault.getIsDefault()).isFalse();
        assertThat(normalAddress.getIsDefault()).isFalse();
    }

    private User user() {
        User user = new User();
        user.setId(1L);
        user.setUsername("customer");
        user.setEmail("customer@example.com");
        user.setBalance(BigDecimal.ZERO);
        return user;
    }

    private Address address(User user, Long id, boolean isDefault) {
        Address address = new Address();
        address.setId(id);
        address.setUser(user);
        address.setContactName("张三");
        address.setPhone("13800000000");
        address.setEmail("receiver@example.com");
        address.setAddress("上海市浦东新区");
        address.setIsDefault(isDefault);
        return address;
    }
}

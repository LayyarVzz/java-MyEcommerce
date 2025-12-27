package com.example.myecommerce.service;

import com.example.myecommerce.entity.Address;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    public List<Address> getUserAddresses(String username) {
        User user = userService.getCurrentUser(username);
        return addressRepository.findByUser(user);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    public boolean canAddMoreAddresses(String username) {
        User user = userService.getCurrentUser(username);
        return addressRepository.countByUser(user) < 3;
    }

    public void saveAddress(String username, Address address) {
        User user = userService.getCurrentUser(username);
        address.setUser(user);

        // 如果这是用户第一个地址，设为默认地址
        if (addressRepository.countByUser(user) == 0) {
            address.setIsDefault(true);
        }
        // 如果用户设置此地址为默认地址
        else if (Boolean.TRUE.equals(address.getIsDefault())) {
            // 取消其他地址的默认状态
            addressRepository.unsetDefaultAddresses(user);
        }

        addressRepository.save(address);
    }

    // AddressService.java
    public void updateAddress(String username, Address address) {
        User user = userService.getCurrentUser(username);
        address.setUser(user);

        // 获取数据库中的原始地址
        Address originalAddress = addressRepository.findById(address.getId())
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        // 如果用户没有明确指定默认状态，则保持原样
        if (address.getIsDefault() == null) {
            address.setIsDefault(originalAddress.getIsDefault());
        }

        // 处理默认地址状态变更
        if (address.getIsDefault() != originalAddress.getIsDefault()) {
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                // 用户想设为默认地址
                addressRepository.unsetDefaultAddresses(user);
            } else {
                // 用户想取消默认地址
                List<Address> userAddresses = addressRepository.findByUser(user);

                // 检查是否还有其他地址
                if (userAddresses.size() <= 1) {
                    // 只有一个地址，不能取消默认状态
                    address.setIsDefault(true);
                } else {
                    // 检查是否还有其他默认地址
                    long otherDefaultCount = userAddresses.stream()
                            .filter(addr -> addr.getIsDefault() && !addr.getId().equals(address.getId()))
                            .count();

                    if (otherDefaultCount == 0) {
                        // 没有其他默认地址，需要先设置另一个地址为默认地址
                        Address otherAddress = userAddresses.stream()
                                .filter(addr -> !addr.getId().equals(address.getId()))
                                .findFirst()
                                .orElse(null);

                        if (otherAddress != null) {
                            otherAddress.setIsDefault(true);
                            addressRepository.save(otherAddress);
                        }
                    }
                }
            }
        }

        addressRepository.save(address);
    }


    public void deleteAddress(String username, Long addressId) {
        User user = userService.getCurrentUser(username);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权删除此地址");
        }

        boolean wasDefault = address.getIsDefault();
        addressRepository.deleteById(addressId);

        // 如果删除的是默认地址，设置最早的地址为默认地址
        if (wasDefault) {
            List<Address> remainingAddresses = addressRepository.findByUser(user);
            if (!remainingAddresses.isEmpty()) {
                // 找到创建时间最早的地址
                Address earliestAddress = remainingAddresses.stream()
                        .min((a1, a2) -> a1.getCreatedAt().compareTo(a2.getCreatedAt()))
                        .orElse(remainingAddresses.get(0));

                earliestAddress.setIsDefault(true);
                addressRepository.save(earliestAddress);
            }
        }
    }

    public void setDefaultAddress(String username, Long addressId) {
        User user = userService.getCurrentUser(username);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权设置此地址为默认地址");
        }

        // 取消其他地址的默认状态
        addressRepository.unsetDefaultAddresses(user);

        // 设置当前地址为默认地址
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    public Address getDefaultAddress(String username) {
        User user = userService.getCurrentUser(username);
        Optional<Address> defaultAddress = addressRepository.findByUserAndIsDefaultTrue(user);

        // 如果没有默认地址，返回最早的地址
        if (defaultAddress.isEmpty()) {
            List<Address> addresses = addressRepository.findByUser(user);
            if (!addresses.isEmpty()) {
                return addresses.stream()
                        .min((a1, a2) -> a1.getCreatedAt().compareTo(a2.getCreatedAt()))
                        .orElse(addresses.get(0));
            }
        }

        return defaultAddress.orElse(null);
    }
}

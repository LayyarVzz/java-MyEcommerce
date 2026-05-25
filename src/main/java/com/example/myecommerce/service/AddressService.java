package com.example.myecommerce.service;

import com.example.myecommerce.entity.Address;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    @Transactional
    public List<Address> getUserAddresses(String username) {
        User user = userService.getCurrentUser(username);
        List<Address> addresses = addressRepository.findByUser(user);
        normalizeDefaultAddress(addresses);
        return addresses;
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

    @Transactional
    public void updateAddress(String username, Address address) {
        User user = userService.getCurrentUser(username);
        address.setUser(user);

        Address originalAddress = addressRepository.findById(address.getId())
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!originalAddress.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权修改此地址");
        }

        if (address.getIsDefault() == null) {
            address.setIsDefault(originalAddress.getIsDefault());
        }

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.unsetDefaultAddresses(user);
            address.setIsDefault(true);
        } else {
            List<Address> userAddresses = addressRepository.findByUser(user);

            if (userAddresses.size() <= 1) {
                address.setIsDefault(true);
            } else {
                long otherDefaultCount = userAddresses.stream()
                        .filter(addr -> Boolean.TRUE.equals(addr.getIsDefault()) && !addr.getId().equals(address.getId()))
                        .count();

                if (otherDefaultCount == 0) {
                    Address otherAddress = userAddresses.stream()
                            .filter(addr -> !addr.getId().equals(address.getId()))
                            .findFirst()
                            .orElse(null);

                    if (otherAddress != null) {
                        otherAddress.setIsDefault(true);
                        addressRepository.save(otherAddress);
                    }
                }
                address.setIsDefault(false);
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

    @Transactional
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
        List<Address> addresses = addressRepository.findByUser(user);
        normalizeDefaultAddress(addresses);

        return addresses.stream()
                .filter(address -> Boolean.TRUE.equals(address.getIsDefault()))
                .findFirst()
                .orElse(null);
    }

    private void normalizeDefaultAddress(List<Address> addresses) {
        if (addresses.isEmpty()) {
            return;
        }

        Address selectedDefault = addresses.stream()
                .filter(address -> Boolean.TRUE.equals(address.getIsDefault()))
                .min(Comparator.comparing(Address::getCreatedAt))
                .orElseGet(() -> addresses.stream()
                        .min(Comparator.comparing(Address::getCreatedAt))
                        .orElse(addresses.get(0)));

        for (Address address : addresses) {
            boolean shouldBeDefault = address.getId().equals(selectedDefault.getId());
            if (!Boolean.valueOf(shouldBeDefault).equals(address.getIsDefault())) {
                address.setIsDefault(shouldBeDefault);
                addressRepository.save(address);
            }
        }
    }
}

package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Address;
import com.example.myecommerce.service.AddressService;
import com.example.myecommerce.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @GetMapping
    public String listAddresses(Model model, Authentication authentication) {
        String username = authentication.getName();
        com.example.myecommerce.entity.User user = userService.getCurrentUser(username);
        model.addAttribute("addresses", addressService.getUserAddresses(username));
        model.addAttribute("canAddMore", addressService.canAddMoreAddresses(username));
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        return "address-list";
    }

    @GetMapping("/add")
    public String showAddAddressForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        if (!addressService.canAddMoreAddresses(username)) {
            return "redirect:/addresses";
        }

        Address address = new Address();
        address.setIsDefault(false); // 明确设置默认值
        com.example.myecommerce.entity.User user = userService.getCurrentUser(username);
        model.addAttribute("address", address);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        return "address-form";
    }

    @PostMapping({"/add", ""})
    public String addAddress(
            @RequestParam String contactName,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false, defaultValue = "false") Boolean isDefault,
            Authentication authentication) {
        String username = authentication.getName();
        if (!addressService.canAddMoreAddresses(username)) {
            return "redirect:/addresses";
        }

        Address addr = new Address();
        addr.setContactName(contactName);
        addr.setPhone(phone);
        addr.setAddress(address);
        addr.setIsDefault(isDefault != null ? isDefault : false);

        addressService.saveAddress(username, addr);
        return "redirect:/addresses";
    }

    @GetMapping("/edit/{id}")
    public String showEditAddressForm(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        Address address = addressService.getAddressById(id);

        if (address == null || !address.getUser().getUsername().equals(username)) {
            return "redirect:/addresses";
        }

        com.example.myecommerce.entity.User user = userService.getCurrentUser(username);
        model.addAttribute("address", address);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        model.addAttribute("isOnlyAddress", addressService.getUserAddresses(username).size() <= 1);
        return "address-form";
    }

    @PostMapping({"/edit/{id}", "/{id}"})
    public String editAddress(
            @PathVariable Long id,
            @RequestParam String contactName,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) Boolean isDefault,
            Authentication authentication) {

        String username = authentication.getName();
        Address existingAddress = addressService.getAddressById(id);

        if (existingAddress == null || !existingAddress.getUser().getUsername().equals(username)) {
            return "redirect:/addresses";
        }

        existingAddress.setContactName(contactName);
        existingAddress.setPhone(phone);
        existingAddress.setAddress(address);

        // 始终设置 isDefault，没勾选就是 false
        existingAddress.setIsDefault(Boolean.TRUE.equals(isDefault));

        addressService.updateAddress(username, existingAddress);
        return "redirect:/addresses";
    }


    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        addressService.deleteAddress(username, id);
        return "redirect:/addresses";
    }

    @GetMapping("/default/{id}")
    public String setDefaultAddress(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        addressService.setDefaultAddress(username, id);
        return "redirect:/addresses";
    }
}

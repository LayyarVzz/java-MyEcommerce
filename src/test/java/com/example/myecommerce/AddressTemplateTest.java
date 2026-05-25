package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTemplateTest {

    @Test
    void addressPagesUseRecipientEmailPerShippingAddress() throws IOException {
        String listHtml = Files.readString(Path.of("src/main/resources/templates/address-list.html"));
        String formHtml = Files.readString(Path.of("src/main/resources/templates/address-form.html"));
        String checkoutHtml = Files.readString(Path.of("src/main/resources/templates/checkout.html"));

        assertThat(formHtml).contains("收件邮箱");
        assertThat(formHtml).contains("type=\"email\"");
        assertThat(formHtml).contains("th:field=\"*{email}\"");
        assertThat(formHtml).contains("addressError");

        assertThat(listHtml).contains("th:text=\"${address.email}\"");
        assertThat(listHtml).doesNotContain("th:action=\"@{/addresses/email}\"");
        assertThat(listHtml).doesNotContain("th:text=\"${accountEmail}\"");

        assertThat(checkoutHtml).contains("th:text=\"${address.email}\"");
        assertThat(checkoutHtml).doesNotContain("th:text=\"${address.user.email}\"");
    }
}

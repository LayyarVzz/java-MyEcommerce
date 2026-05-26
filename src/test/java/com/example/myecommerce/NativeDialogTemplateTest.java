package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NativeDialogTemplateTest {

    private static final Pattern NATIVE_DIALOG_CALL =
            Pattern.compile("\\b(alert|confirm|prompt)\\s*\\(");

    @Test
    void pagesDoNotUseBrowserNativeDialogs() throws IOException {
        List<Path> files;
        try (Stream<Path> paths = Stream.concat(
                Files.walk(Path.of("src/main/resources/templates")),
                Files.walk(Path.of("src/main/resources/static/js"))
        )) {
            files = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".html") || path.toString().endsWith(".js"))
                    .toList();
        }

        for (Path file : files) {
            String source = Files.readString(file);
            assertThat(NATIVE_DIALOG_CALL.matcher(source).find())
                    .as("native browser dialog call in %s", file)
                    .isFalse();
        }
    }

    @Test
    void sharedShellsLoadPlatformConfirmDialogAssets() throws IOException {
        String storefrontHead = Files.readString(Path.of("src/main/resources/templates/fragments/navbar.html"));
        String adminHead = Files.readString(Path.of("src/main/resources/templates/fragments/admin-shell.html"));
        String salesHead = Files.readString(Path.of("src/main/resources/templates/fragments/sales-shell.html"));

        for (String head : List.of(storefrontHead, adminHead, salesHead)) {
            assertThat(head).contains("@{/css/confirm-dialog.css}");
            assertThat(head).contains("@{/js/platform-confirm.js}");
        }
    }

    @Test
    void destructiveActionsUsePlatformConfirmAttributes() throws IOException {
        List<Path> files = List.of(
                Path.of("src/main/resources/templates/order-history.html"),
                Path.of("src/main/resources/templates/order-detail.html"),
                Path.of("src/main/resources/templates/address-list.html"),
                Path.of("src/main/resources/templates/sales/product-list.html"),
                Path.of("src/main/resources/templates/admin/customer-list.html"),
                Path.of("src/main/resources/templates/admin/customer-detail.html")
        );

        for (Path file : files) {
            String source = Files.readString(file);
            assertThat(source)
                    .as("platform confirm attributes in %s", file)
                    .contains("data-confirm-title")
                    .contains("data-confirm-message");
        }
    }
}

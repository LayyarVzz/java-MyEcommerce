package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationConfigPrivacyTest {

    @Test
    void applicationImportsIgnoredDotEnvForMailPasswordOnly() throws IOException {
        String yaml = Files.readString(Path.of("src/main/resources/application.yaml"));
        String gitignore = Files.readString(Path.of(".gitignore"));
        String example = Files.readString(Path.of(".env.example"));

        assertThat(yaml).contains("import: optional:file:.env[.properties]");
        assertThat(yaml).contains("host: ${MAIL_HOST:smtp.qq.com}");
        assertThat(yaml).contains("port: ${MAIL_PORT:465}");
        assertThat(yaml).contains("username: ${MAIL_USERNAME:1486234558@qq.com}");
        assertThat(yaml).contains("password: ${MAIL_PASSWORD:}");
        assertThat(yaml).contains("nickname: ${MAIL_NICKNAME:MyEcommerce}");
        assertThat(yaml).doesNotContain("${SERVER_PORT");
        assertThat(yaml).doesNotContain("${DB_");
        assertThat(yaml).doesNotContain("${PRODUCT_IMAGES_");

        assertThat(gitignore).contains(".env");
        assertThat(gitignore).contains("!.env.example");

        assertThat(example).contains("MAIL_PASSWORD=");
        assertThat(example).doesNotContain("1486234558");
    }

    @Test
    void mailConfigurationUsesAuthenticatedSslSmtp() throws IOException {
        String yaml = Files.readString(Path.of("src/main/resources/application.yaml"));

        assertThat(yaml).contains("protocol: smtp");
        assertThat(yaml).contains("\"[mail.smtp.auth]\": true");
        assertThat(yaml).contains("\"[mail.smtp.ssl.enable]\": true");
        assertThat(yaml).contains("\"[mail.smtp.connectiontimeout]\": 5000");
        assertThat(yaml).doesNotContain("protocol: smtps");
    }

    @Test
    void dockerComposePassesIgnoredDotEnvToApplicationContainer() throws IOException {
        String compose = Files.readString(Path.of("docker-compose.yml"));

        assertThat(compose).contains("env_file:");
        assertThat(compose).contains("- .env");
    }
}

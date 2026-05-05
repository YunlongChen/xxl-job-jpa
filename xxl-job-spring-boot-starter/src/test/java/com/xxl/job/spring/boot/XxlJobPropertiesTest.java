package com.xxl.job.spring.boot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for XxlJobProperties configuration binding.
 */
public class XxlJobPropertiesTest {

    @Test
    void defaultValuesAreCorrect() {
        XxlJobProperties properties = new XxlJobProperties();

        // Admin defaults
        assertThat(properties.getAdmin().getAddresses()).isNull();
        assertThat(properties.getAdmin().getAccessToken()).isNull();
        assertThat(properties.getAdmin().getTimeout()).isEqualTo(3);

        // Executor defaults
        assertThat(properties.getExecutor().getEnabled()).isTrue();
        assertThat(properties.getExecutor().getAppname()).isNull();
        assertThat(properties.getExecutor().getPort()).isEqualTo(9999);
        assertThat(properties.getExecutor().getLogRetentionDays()).isEqualTo(-1);
        assertThat(properties.getExecutor().getExcludedPackage()).isEqualTo("org.springframework.,spring.");

        // Top-level defaults
        assertThat(properties.getTimeout()).isEqualTo(3);
        assertThat(properties.getI18n()).isEqualTo("zh_CN");
        assertThat(properties.getLogRetentionDays()).isEqualTo(30);
        assertThat(properties.getScheduleBatchSize()).isEqualTo(100);
    }

    @Test
    void nestedPropertiesCanBeSet() {
        XxlJobProperties properties = new XxlJobProperties();

        properties.setAccessToken("my-token");
        properties.setTimeout(5);

        properties.getAdmin().setAddresses("http://localhost:8080");
        properties.getAdmin().setAccessToken("admin-token");
        properties.getAdmin().setTimeout(10);

        properties.getExecutor().setAppname("test-executor");
        properties.getExecutor().setPort(9998);
        properties.getExecutor().setEnabled(false);

        properties.getTriggerPool().setFastMax(500);
        properties.getTriggerPool().setSlowMax(200);

        // Verify admin
        assertThat(properties.getAdmin().getAddresses()).isEqualTo("http://localhost:8080");
        assertThat(properties.getAdmin().getAccessToken()).isEqualTo("admin-token");
        assertThat(properties.getAdmin().getTimeout()).isEqualTo(10);

        // Verify executor
        assertThat(properties.getExecutor().getAppname()).isEqualTo("test-executor");
        assertThat(properties.getExecutor().getPort()).isEqualTo(9998);
        assertThat(properties.getExecutor().getEnabled()).isFalse();

        // Verify trigger pool
        assertThat(properties.getTriggerPool().getFastMax()).isEqualTo(500);
        assertThat(properties.getTriggerPool().getSlowMax()).isEqualTo(200);

        // Top-level (fallback)
        assertThat(properties.getAccessToken()).isEqualTo("my-token");
        assertThat(properties.getTimeout()).isEqualTo(5);
    }

    @Test
    void adminAccessTokenFallsBackToTopLevel() {
        XxlJobProperties properties = new XxlJobProperties();

        // Only set top-level access token
        properties.setAccessToken("top-level-token");

        // Admin access token not set, should fall back to top-level
        assertThat(properties.getAdmin().getAccessToken()).isNull(); // Admin defaults to null

        // When used in auto-config, it should check admin first, then fall back to top-level
        String effectiveToken = properties.getAdmin().getAccessToken() != null
                ? properties.getAdmin().getAccessToken()
                : properties.getAccessToken();
        assertThat(effectiveToken).isEqualTo("top-level-token");
    }
}
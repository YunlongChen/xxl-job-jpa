package com.xxl.job.spring.boot;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for xxl-job-spring-boot-starter auto-configuration.
 */
public class XxlJobAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(XxlJobExecutorAutoConfiguration.class));

    @Test
    void autoConfigurationIsApplied() {
        this.contextRunner
                .withPropertyValues(
                        "xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin",
                        "xxl.job.executor.appname=test-app"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(XxlJobSpringExecutor.class);
                    XxlJobSpringExecutor executor = context.getBean(XxlJobSpringExecutor.class);
                    assertThat(executor).isNotNull();
                });
    }

    @Test
    void autoConfigurationIsNotAppliedWhenAdminAddressesMissing() {
        this.contextRunner
                .withPropertyValues(
                        "xxl.job.executor.appname=test-app"
                )
                .run(context -> {
                    // Without admin addresses, the condition @ConditionalOnProperty fails
                    // so no executor bean should be created
                    assertThat(context).doesNotHaveBean(XxlJobSpringExecutor.class);
                });
    }

    @Test
    void manualExecutorBeanTakesPrecedence() {
        this.contextRunner
                .withPropertyValues(
                        "xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin",
                        "xxl.job.executor.appname=test-app"
                )
                .withUserConfiguration(ManualExecutorConfig.class)
                .run(context -> {
                    // Manual bean should exist, auto-configured bean should NOT be created
                    assertThat(context).hasSingleBean(XxlJobSpringExecutor.class);
                    XxlJobSpringExecutor executor = context.getBean(XxlJobSpringExecutor.class);
                    assertThat(executor.getAppname()).isEqualTo("manual-app");
                });
    }

    @Test
    void healthIndicatorIsCreatedByDefault() {
        this.contextRunner
                .withPropertyValues(
                        "xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin",
                        "xxl.job.executor.appname=test-app",
                        "xxl.job.health-indicator-enabled=true"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(XxlJobHealthIndicator.class);
                });
    }

    @Test
    void healthIndicatorCanBeDisabled() {
        this.contextRunner
                .withPropertyValues(
                        "xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin",
                        "xxl.job.executor.appname=test-app",
                        "xxl.job.health-indicator-enabled=false"
                )
                .run(context -> {
                    assertThat(context).doesNotHaveBean(XxlJobHealthIndicator.class);
                });
    }

    @Configuration
    static class ManualExecutorConfig {
        @Bean
        public XxlJobSpringExecutor manualExecutor() {
            XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
            executor.setAppname("manual-app");
            return executor;
        }
    }
}
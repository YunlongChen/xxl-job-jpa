package com.xxl.job.spring.boot;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

/**
 * Health indicator for xxl-job executor.
 * Reports UP if the executor has been initialized.
 */
public class XxlJobHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        XxlJobSpringExecutor executor = XxlJobSpringExecutor.getInstance();

        if (executor == null) {
            return Health.down()
                    .withDetail("reason", "XxlJobSpringExecutor not initialized")
                    .build();
        }

        String appName = executor.getAppname();
        if (appName != null && !appName.isEmpty()) {
            return Health.up()
                    .withDetail("appname", appName)
                    .build();
        }

        return Health.unknown()
                .withDetail("reason", "XxlJobSpringExecutor initialized but not fully started")
                .build();
    }
}
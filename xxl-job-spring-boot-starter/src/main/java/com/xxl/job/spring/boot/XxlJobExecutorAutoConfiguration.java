package com.xxl.job.spring.boot;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.spring.boot.annotation.EnableXxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for xxl-job executor.
 * <p>
 * Activated when:
 * <ul>
 *   <li>{@link EnableXxlJob} annotation is present on the configuration class</li>
 *   <li>No existing {@link XxlJobSpringExecutor} bean is defined</li>
 *   <li>Property {@code xxl.job.admin.addresses} is set</li>
 * </ul>
 */
@AutoConfiguration
@EnableConfigurationProperties(XxlJobProperties.class)
@ConditionalOnClass(XxlJobSpringExecutor.class)
@ConditionalOnProperty(prefix = "xxl.job.admin", value = "addresses")
public class XxlJobExecutorAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutorAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(XxlJobSpringExecutor.class)
    public XxlJobSpringExecutor xxlJobExecutor(XxlJobProperties properties) {
        logger.info(">>>>>>>>>>> xxl-job auto-configuration start.");

        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();

        // Admin properties
        String adminAddresses = properties.getAdmin().getAddresses();
        String accessToken = properties.getAdmin().getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            accessToken = properties.getAccessToken();
        }
        int timeout = properties.getAdmin().getTimeout();
        if (timeout <= 0) {
            timeout = properties.getTimeout();
        }

        executor.setAdminAddresses(adminAddresses);
        executor.setAccessToken(accessToken);
        executor.setTimeout(timeout);

        // Executor properties
        XxlJobProperties.ExecutorProperties execProps = properties.getExecutor();
        executor.setEnabled(execProps.getEnabled());
        executor.setAppname(execProps.getAppname());
        executor.setAddress(execProps.getAddress());
        executor.setIp(execProps.getIp());
        executor.setPort(execProps.getPort());
        executor.setLogPath(execProps.getLogPath());
        executor.setLogRetentionDays(execProps.getLogRetentionDays());
        executor.setExcludedPackage(execProps.getExcludedPackage());

        logger.info(">>>>>>>>>>> xxl-job auto-configuration completed. executor appname: {}", execProps.getAppname());
        return executor;
    }

    @Bean
    @ConditionalOnProperty(prefix = "xxl.job", name = "health-indicator-enabled", havingValue = "true", matchIfMissing = true)
    public XxlJobHealthIndicator xxlJobHealthIndicator() {
        return new XxlJobHealthIndicator();
    }
}
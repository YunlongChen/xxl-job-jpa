package com.xxl.job.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for xxl-job executor.
 * <p>
 * Maps to properties under prefix "xxl.job" with nested prefixes
 * "xxl.job.admin", "xxl.job.executor", and "xxl.job.triggerpool".
 */
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    // ---------------------- Top-level properties ----------------------

    private String accessToken;
    private int timeout = 3;
    private String i18n = "zh_CN";

    private TriggerPool triggerPool = new TriggerPool();
    private int scheduleBatchSize = 100;
    private int logRetentionDays = 30;

    // ---------------------- Nested: AdminProperties (xxl.job.admin.*) ----------------------

    private AdminProperties admin = new AdminProperties();

    // ---------------------- Nested: ExecutorProperties (xxl.job.executor.*) ----------------------

    private ExecutorProperties executor = new ExecutorProperties();

    // ---------------------- Getters and Setters ----------------------

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getI18n() {
        return i18n;
    }

    public void setI18n(String i18n) {
        this.i18n = i18n;
    }

    public TriggerPool getTriggerPool() {
        return triggerPool;
    }

    public void setTriggerPool(TriggerPool triggerPool) {
        this.triggerPool = triggerPool;
    }

    public int getScheduleBatchSize() {
        return scheduleBatchSize;
    }

    public void setScheduleBatchSize(int scheduleBatchSize) {
        this.scheduleBatchSize = scheduleBatchSize;
    }

    public int getLogRetentionDays() {
        return logRetentionDays;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    public AdminProperties getAdmin() {
        return admin;
    }

    public void setAdmin(AdminProperties admin) {
        this.admin = admin;
    }

    public ExecutorProperties getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorProperties executor) {
        this.executor = executor;
    }

    // ---------------------- Nested: AdminProperties ----------------------

    public static class AdminProperties {
        private String addresses;
        private String accessToken;
        private int timeout = 3;

        public String getAddresses() {
            return addresses;
        }

        public void setAddresses(String addresses) {
            this.addresses = addresses;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

    // ---------------------- Nested: ExecutorProperties ----------------------

    public static class ExecutorProperties {
        private Boolean enabled = true;
        private String appname;
        private String address;
        private String ip;
        private int port = 9999;
        private String logPath;
        private int logRetentionDays = -1;
        private String excludedPackage = "org.springframework.,spring.";

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getLogPath() {
            return logPath;
        }

        public void setLogPath(String logPath) {
            this.logPath = logPath;
        }

        public int getLogRetentionDays() {
            return logRetentionDays;
        }

        public void setLogRetentionDays(int logRetentionDays) {
            this.logRetentionDays = logRetentionDays;
        }

        public String getExcludedPackage() {
            return excludedPackage;
        }

        public void setExcludedPackage(String excludedPackage) {
            this.excludedPackage = excludedPackage;
        }
    }

    // ---------------------- Nested: TriggerPool ----------------------

    public static class TriggerPool {
        private int fastMax = 200;
        private int slowMax = 100;

        public int getFastMax() {
            return fastMax;
        }

        public void setFastMax(int fastMax) {
            this.fastMax = fastMax;
        }

        public int getSlowMax() {
            return slowMax;
        }

        public void setSlowMax(int slowMax) {
            this.slowMax = slowMax;
        }
    }
}
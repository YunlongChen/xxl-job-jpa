# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Full build (skip tests)
mvn clean package -Dmaven.test.skip=true

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=XxlJobInfoMapperTest -pl xxl-job-admin

# Run a specific test method
mvn test -Dtest=XxlJobInfoMapperTest#pageList -pl xxl-job-admin

# Build specific module
mvn clean package -pl xxl-job-core -Dmaven.test.skip=true
```

## Architecture

XXL-JOB is a distributed task scheduling framework with two main components:

- **xxl-job-admin** (Scheduling Center): Web UI + scheduling logic. Persists job definitions, triggers executions, and monitors results.
- **xxl-job-core** (Executor Library): Embedded in client applications. Receives schedule requests and executes job handlers.
- **xxl-job-executor-samples**: Example executor applications demonstrating integration with xxl-job-core.

### Storage Layer (This Fork)

This is a **fork of the original XXL-JOB** that migrated the scheduling center's storage layer from MyBatis to JPA (Hibernate).

**Key architectural notes:**
- **Dual persistence approach**: The admin module uses both JPA `Repository` interfaces (for the fork's JPA implementation) and MyBatis `Mapper` interfaces (for backward compatibility with existing tests/integrations). When adding new data access code, use the JPA repositories in `com.xxl.job.admin.repository`.
- **Production database**: Currently configured for PostgreSQL (`jdbc:postgresql://192.168.3.112:5432/xxl-job`)
- **Test database**: H2 in-memory with MySQL compatibility mode (`jdbc:h2:mem:xxl_job;MODE=MySQL`)

### Job Handler Development

Job handlers are implemented in executor applications using the `@JobHandler` annotation:

```java
@JobHandler(value = "myJobHandler")
@Bean
public class MyJobHandler extends IJobHandler {
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        // job logic
        return ReturnT.SUCCESS;
    }
}
```

## Module Structure

```
xxl-job/
├── xxl-job-core/                    # Shared executor library
│   └── com.xxl.job.core/
│       ├── handler/                 # IJobHandler, annotations (@JobHandler, @XxlJob)
│       ├── executor/                # XxlJobExecutor, XxlJobSpringExecutor
│       ├── openapi/                 # AdminBiz, ExecutorBiz APIs
│       └── thread/                  # JobThread, TriggerCallbackThread
├── xxl-job-admin/                   # Scheduling center (Spring Boot app)
│   └── com.xxl.job.admin/
│       ├── controller/              # REST controllers (JobInfoController, etc.)
│       ├── repository/              # JPA repositories (this fork's approach)
│       ├── mapper/                  # MyBatis mappers (legacy compatibility)
│       ├── scheduler/               # Scheduling logic (JobScheduleHelper, triggers)
│       └── service/                 # Business services
├── xxl-job-spring-boot-starter/     # Spring Boot auto-configuration (on spring-starter branch)
│   └── com.xxl.job.spring.boot/
│       ├── XxlJobProperties.java           # @ConfigurationProperties
│       ├── XxlJobExecutorAutoConfiguration.java  # Auto-config
│       └── XxlJobHealthIndicator.java      # Actuator health check
└── xxl-job-executor-samples/        # Example executors
    ├── xxl-job-executor-sample-springboot/
    ├── xxl-job-executor-sample-springboot-ai/  # AI tasks (Spring AI, Ollama, Dify)
    └── xxl-job-executor-sample-frameless/
```

## Test Configuration

Tests in `xxl-job-admin/src/test` use H2 in-memory database:
- `application.properties` configures H2 with MySQL mode
- `data.sql` seeds initial test data (executor group, sample job, admin user)
- `xxl.job.admin.enabled=false` disables the scheduling thread pool during tests

Admin password (test): `admin` / `8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92` (SHA-256 of "123456")

## Spring Boot Starter (spring-starter branch)

The `xxl-job-spring-boot-starter` module provides auto-configuration for Spring Boot applications.

### Quick Start

1. Add dependency:
```xml
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-spring-boot-starter</artifactId>
    <version>3.4.1-SNAPSHOT</version>
</dependency>
```

2. Configure `application.properties`:
```properties
xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin
xxl.job.executor.appname=my-app
```

3. Use `@EnableXxlJob` annotation (optional):
```java
@EnableXxlJob
@SpringBootApplication
public class MyApplication { ... }
```

### Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `xxl.job.admin.addresses` | (required) | Admin server addresses |
| `xxl.job.admin.accessToken` | - | Access token |
| `xxl.job.admin.timeout` | 3 | RPC timeout (seconds) |
| `xxl.job.executor.appname` | (required) | Executor app name |
| `xxl.job.executor.port` | 9999 | Server port |
| `xxl.job.executor.enabled` | true | Enable executor |
| `xxl.job.executor.logPath` | - | Log path |
| `xxl.job.executor.logRetentionDays` | -1 | Log retention days |
| `xxl.job.executor.excludedPackage` | spring.,org.springframework. | Packages to exclude |
| `xxl.job.health-indicator-enabled` | true | Enable actuator health indicator |

### Run Tests

```bash
# Run starter module tests
mvn test -pl xxl-job-spring-boot-starter

# Run sample module tests
mvn test -pl xxl-job-executor-samples/xxl-job-executor-sample-springboot
```
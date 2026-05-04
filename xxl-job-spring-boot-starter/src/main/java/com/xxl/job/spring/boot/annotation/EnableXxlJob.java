package com.xxl.job.spring.boot.annotation;

import java.lang.annotation.*;

/**
 * Enable xxl-job executor auto-configuration.
 * <p>
 * Place this annotation on a {@link org.springframework.context.annotation.Configuration} class
 * or your main application class to enable the xxl-job starter auto-configuration.
 * <p>
 * Alternatively, you can rely on the auto-configuration being activated by the
 * presence of {@code xxl.job.admin.addresses} property, provided the starter
 * is on the classpath.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableXxlJob {
}
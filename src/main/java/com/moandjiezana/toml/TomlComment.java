package com.moandjiezana.toml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define comments for TOML objects
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TomlComment {
  /**
   * Comment(s) to add. Supports multilined comments with '\n' or by providing multiple Strings
   */
  String[] value();
}

package com.moandjiezana.toml;

interface ValueConverter {

  /**
   * @param s must already have been trimmed
   */
  boolean canConvert(String s);
  
  /**
   * @param s must already have been validated by {@link #canConvert(String)}
   */
  Object convert(String s);
}

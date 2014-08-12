package com.moandjiezana.toml;

interface ValueParser {

  /**
   * @param s must already have been trimmed
   */
  boolean canParse(String s);
  
  /**
   * @param s must already have been validated by {@link #canParse(String)}
   */
  Object parse(String s);
}

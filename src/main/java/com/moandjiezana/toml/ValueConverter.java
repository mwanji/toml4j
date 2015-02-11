package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

interface ValueConverter {

  /**
   * @param s must already have been trimmed
   */
  boolean canConvert(String s);
  
  /**
   * @param s must already have been validated by {@link #canConvert(String)}
   * @return a value or {@link ValueConverterUtils#INVALID}
   */
  Object convert(String s);

  /**
   * Partial validation. Stops after type terminator, rather than at EOI.
   * 
   * @param s  must already have been validated by {@link #canConvert(String)}
   * @param index where to start in s
   * @return a value or {@link ValueConverterUtils#INVALID}
   */
  Object convert(String s, AtomicInteger index);
}

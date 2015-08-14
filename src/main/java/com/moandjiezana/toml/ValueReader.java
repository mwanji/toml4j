package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

interface ValueReader {

  /**
   * @param s must already have been trimmed
   */
  boolean canRead(String s);
  
  /**
   * Partial validation. Stops after type terminator, rather than at EOI.
   * 
   * @param s  must already have been validated by {@link #canRead(String)}
   * @param index where to start in s
   * @param line current line number, used for error reporting 
   * @return a value or a {@link Results.Errors}
   */
  Object read(String s, AtomicInteger index, Context context);
}

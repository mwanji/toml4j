package com.moandjiezana.toml;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

class LocalDateTimeValueReaderWriter implements ValueReader, ValueWriter {

  static final LocalDateTimeValueReaderWriter LOCAL_DATE_TIME_VALUE_READER_WRITER = new LocalDateTimeValueReaderWriter();

  @Override
  public boolean canRead(String s) {
    int dash = 0;
    int t = 0;
    int dp = 0;
    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);
      if (c == '\n') {
        break;
      }
      else if (c == '-') {
        dash++;
      } else if (c == 'T') {
        t++;
      } else if (c == ':') {
        dp++;
      }
      if (dash == 2 && t == 1 && dp == 2) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Object read(String original, AtomicInteger index, Context context) {
    StringBuilder sb = new StringBuilder();
    for (int i = index.get(); i < original.length(); i = index.incrementAndGet()) {
      char c = original.charAt(i);
      if (c == '\n') {
        break;
      }
      if (c != ' ' && c != '\t') {
        sb.append(c);
      }
    }

    index.decrementAndGet();

    try {
      return LocalDateTime.parse(sb.toString());
    } catch (Exception ignored) {
      Results.Errors errors = new Results.Errors();
      errors.invalidKey(context.identifier.getName(), context.line.get());
      return errors;
    }
  }

  @Override
  public boolean canWrite(Object value) {
    return value instanceof LocalDateTime;
  }

  @Override
  public void write(Object value, WriterContext context) {
    LocalDateTime val = (LocalDateTime) value;
    context.write(val.toString());
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  @Override
  public String toString() {
    return "local datetime";
  }
}

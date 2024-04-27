package com.moandjiezana.toml;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

class LocalTimeValueReaderWriter implements ValueReader, ValueWriter {

  static final LocalTimeValueReaderWriter LOCAL_TIME_VALUE_READER_WRITER = new LocalTimeValueReaderWriter();

  @Override
  public boolean canRead(String s) {
    int dp = 0;
    int p = 0;
    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);
      if (c == '\n') {
        break;
      }
      else if (c == ':') {
        dp++;
      }
      else if (c == '.') {
        p++;
      }

      if (dp == 2 && p < 2) {
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
      return LocalTime.parse(sb.toString());
    } catch (Exception ignored) {
      Results.Errors errors = new Results.Errors();
      errors.invalidKey(context.identifier.getName(), context.line.get());
      return errors;
    }
  }

  @Override
  public boolean canWrite(Object value) {
    return value instanceof LocalTime;
  }

  @Override
  public void write(Object value, WriterContext context) {
    LocalTime val = (LocalTime) value;
    context.write(val.toString());
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  @Override
  public String toString() {
    return "local time";
  }
}

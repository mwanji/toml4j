package com.moandjiezana.toml;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

class LocalDateValueReaderWriter implements ValueReader, ValueWriter {

  static final LocalDateValueReaderWriter LOCAL_DATE_VALUE_READER_WRITER = new LocalDateValueReaderWriter();

  @Override
  public boolean canRead(String s) {
    // not confuse with Number
    if (s.charAt(0) == '-') return false;

    int dash = 0;
    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);
      if (c == '\n') {
        break;
      }
      else if (c == '-') {
        dash++;
      }

      if (dash == 2) {
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
      return LocalDate.parse(sb.toString());
    } catch (Exception ignored) {
      Results.Errors errors = new Results.Errors();
      errors.invalidKey(context.identifier.getName(), context.line.get());
      return errors;
    }
  }

  @Override
  public boolean canWrite(Object value) {
    return value instanceof LocalDate;
  }

  @Override
  public void write(Object value, WriterContext context) {
    LocalDate val = (LocalDate) value;
    context.write(val.toString());
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  @Override
  public String toString() {
    return "local date";
  }
}

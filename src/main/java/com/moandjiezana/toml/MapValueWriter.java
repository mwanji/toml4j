package com.moandjiezana.toml;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.moandjiezana.toml.PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER;
import static com.moandjiezana.toml.TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER;
import static com.moandjiezana.toml.ValueWriters.WRITERS;

class MapValueWriter implements ValueWriter {
  static final ValueWriter MAP_VALUE_WRITER = new MapValueWriter();

  private static final Pattern requiredQuotingPattern = Pattern.compile("^.*[^A-Za-z\\d_-].*$");

  @Override
  public boolean canWrite(Object value) {
    return value instanceof Map;
  }

  @Override
  public void write(Object value, WriterContext context) {
    Map from = (Map) value;

    if (hasPrimitiveValues(from)) {
      context.writeKey();
    }

    // Render primitive types and arrays of primitive first so they are
    // grouped under the same table (if there is one)
    for (Object key : from.keySet()) {
      Object fromValue = from.get(key);
      if (fromValue == null) {
        continue;
      }

      ValueWriter valueWriter = WRITERS.findWriterFor(fromValue);
      if (valueWriter.isPrimitiveType()) {
        context.indent();
        context.output.append(quoteKey(key)).append(" = ");
        valueWriter.write(fromValue, context);
        context.output.append('\n');
      } else if (valueWriter == PRIMITIVE_ARRAY_VALUE_WRITER) {
        context.output.append(quoteKey(key)).append(" = ");
        valueWriter.write(fromValue, context);
        context.output.append('\n');
      }
    }

    // Now render (sub)tables and arrays of tables
    for (Object key : from.keySet()) {
      Object fromValue = from.get(key);
      if (fromValue == null) {
        continue;
      }

      ValueWriter valueWriter = WRITERS.findWriterFor(fromValue);
      if (valueWriter.isTable() || valueWriter == TABLE_ARRAY_VALUE_WRITER) {
        valueWriter.write(fromValue, context.extend(quoteKey(key)));
      }
    }
  }

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

  @Override
  public boolean isTable() {
    return true;
  }

  private static String quoteKey(Object key) {
    String stringKey = key.toString();
    Matcher matcher = requiredQuotingPattern.matcher(stringKey);
    if (matcher.matches()) {
      stringKey = "\"" + stringKey + "\"";
    }

    return stringKey;
  }

  private static boolean hasPrimitiveValues(Map values) {
    for (Object key : values.keySet()) {
      Object fromValue = values.get(key);
      if (fromValue == null) {
        continue;
      }

      ValueWriter valueWriter = WRITERS.findWriterFor(fromValue);
      if (valueWriter.isPrimitiveType() || valueWriter == PRIMITIVE_ARRAY_VALUE_WRITER) {
        return true;
      }
    }

    return false;
  }

  private MapValueWriter() {}
}

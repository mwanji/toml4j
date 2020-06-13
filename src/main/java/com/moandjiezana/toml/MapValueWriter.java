package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.moandjiezana.toml.PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER;
import static com.moandjiezana.toml.TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER;
import static com.moandjiezana.toml.ValueWriters.WRITERS;

class MapValueWriter implements ValueWriter {
  static final ValueWriter MAP_VALUE_WRITER = new MapValueWriter();

  private static final Pattern REQUIRED_QUOTING_PATTERN = Pattern.compile("^.*[^A-Za-z\\d_-].*$");

  private MapValueWriter() {
  }

  private static String quoteKey(Object key) {
    String stringKey = key.toString();
    Matcher matcher = REQUIRED_QUOTING_PATTERN.matcher(stringKey);
    if (matcher.matches()) {
      stringKey = "\"" + stringKey + "\"";
    }

    return stringKey;
  }

  private static boolean hasPrimitiveValues(Map<?, ?> values, WriterContext context) {
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

  @Override
  public boolean canWrite(Object value) {
    return value instanceof Map;
  }

  @Override
  public void write(Object value, WriterContext context) {
    write(value, context, null);
  }

  public void write(Object value, WriterContext context, ArrayList<String[]> comments) {
    Map<?, ?> from = (Map<?, ?>) value;

    if (hasPrimitiveValues(from, context)) {
      context.writeKey();
    }

    int comment = 0;
    // Render primitive types and arrays of primitive first so they are
    // grouped under the same table (if there is one)
    for (Map.Entry<?, ?> entry : from.entrySet()) {
      Object key = entry.getKey();
      Object fromValue = entry.getValue();
      if (fromValue == null) {
        continue;
      }
      final boolean hasComment = (comments != null) && !comments.isEmpty() && comments.get(comment) != null;
      ValueWriter valueWriter = WRITERS.findWriterFor(fromValue);
      if (hasComment) {
        if ((valueWriter == MAP_VALUE_WRITER || valueWriter == ObjectValueWriter.OBJECT_VALUE_WRITER))
          context.write("\n");
        addComments(comments.get(comment), context);
      }
      if (valueWriter.isPrimitiveType()) {
        context.indent();
        context.write(quoteKey(key)).write(" = ");
        valueWriter.write(fromValue, context);
        context.write('\n');
      } else if (valueWriter == PRIMITIVE_ARRAY_VALUE_WRITER) {
        context.setArrayKey(key.toString());
        context.write(quoteKey(key)).write(" = ");
        valueWriter.write(fromValue, context);
        context.write('\n');
      }
      comment++;
    }

    // Now render (sub)tables and arrays of tables
    for (Object key : from.keySet()) {
      Object fromValue = from.get(key);
      if (fromValue == null) {
        continue;
      }

      ValueWriter valueWriter = WRITERS.findWriterFor(fromValue);
      if (valueWriter == this || valueWriter == ObjectValueWriter.OBJECT_VALUE_WRITER || valueWriter == TABLE_ARRAY_VALUE_WRITER) {
        valueWriter.write(fromValue, context.pushTable(quoteKey(key)));
      }
    }
  }

  private void addComments(final String[] comments, WriterContext context) {
    if (comments == null) return;
    for (String comment : comments) {
      if (comment == null) continue;
      if (comment.contains("\n")) {
        final String[] split = comment.split("\n");
        for (String s : split) {
          if (s == null) continue;
          context.indent();
          context.write("# " + s + "\n");
        }
      } else {
        context.indent();
        context.write("# " + comment + "\n");
      }
    }

  }

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

}

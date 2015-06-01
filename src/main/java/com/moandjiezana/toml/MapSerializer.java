package com.moandjiezana.toml;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.moandjiezana.toml.PrimitiveArraySerializer.PRIMITIVE_ARRAY_SERIALIZER;
import static com.moandjiezana.toml.TableArraySerializer.TABLE_ARRAY_SERIALIZER;

class MapSerializer implements Serializer {
  static final Serializer MAP_SERIALIZER = new MapSerializer();

  private static final Pattern requiredQuotingPattern = Pattern.compile("^.*[^A-Za-z\\d_-].*$");

  @Override
  public boolean canSerialize(Object value) {
    return value instanceof Map;
  }

  @Override
  public void serialize(Object value, SerializerContext context) {
    Map from = (Map) value;

    if (hasPrimitiveValues(from)) {
      context.serializeKey();
    }

    // Render primitive types and arrays of primitive first so they are
    // grouped under the same table (if there is one)
    for (Object key : from.keySet()) {
      Object fromValue = from.get(key);
      if (fromValue == null) {
        continue;
      }

      Serializer serializer = Serializers.findSerializerFor(fromValue);
      if (serializer.isPrimitiveType()) {
        context.indent();
        context.serialized.append(quoteKey(key)).append(" = ");
        serializer.serialize(fromValue, context);
        context.serialized.append('\n');
      } else if (serializer == PRIMITIVE_ARRAY_SERIALIZER) {
        context.serialized.append(quoteKey(key)).append(" = ");
        serializer.serialize(fromValue, context);
        context.serialized.append('\n');
      }
    }

    // Now render (sub)tables and arrays of tables
    for (Object key : from.keySet()) {
      Object fromValue = from.get(key);
      if (fromValue == null) {
        continue;
      }

      Serializer serializer = Serializers.findSerializerFor(fromValue);
      if (serializer.isTable() || serializer == TABLE_ARRAY_SERIALIZER) {
        serializer.serialize(fromValue, context.extend(quoteKey(key)));
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

      Serializer serializer = Serializers.findSerializerFor(fromValue);
      if (serializer.isPrimitiveType() || serializer == PRIMITIVE_ARRAY_SERIALIZER) {
        return true;
      }
    }

    return false;
  }
}

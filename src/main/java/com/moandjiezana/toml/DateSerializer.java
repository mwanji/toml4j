package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.Date;

class DateSerializer implements Serializer {
  static final Serializer DATE_SERIALIZER = new DateSerializer();
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:m:ssXXX");

  @Override
  public boolean canSerialize(Object value) {
    return value instanceof Date;
  }

  @Override
  public void serialize(Object value, SerializerContext context) {
    context.serialized.append(dateFormat.format(value));
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  @Override
  public boolean isTable() {
    return false;
  }
}

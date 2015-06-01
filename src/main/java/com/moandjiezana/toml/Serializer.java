package com.moandjiezana.toml;

interface Serializer {
  boolean canSerialize(Object value);

  void serialize(Object value, SerializerContext context);

  boolean isPrimitiveType();

  boolean isTable();
}

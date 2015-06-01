package com.moandjiezana.toml;

class NumberSerializer implements Serializer {
  static final Serializer NUMBER_SERIALIZER = new NumberSerializer();

  @Override
  public boolean canSerialize(Object value) {
    return Number.class.isInstance(value);
  }

  @Override
  public void serialize(Object value, SerializerContext context) {
    context.serialized.append(value.toString());
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

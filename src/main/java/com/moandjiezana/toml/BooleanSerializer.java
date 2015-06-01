package com.moandjiezana.toml;

class BooleanSerializer implements Serializer {
  static final Serializer BOOLEAN_SERIALIZER = new BooleanSerializer();

  @Override
  public boolean canSerialize(Object value) {
    return Boolean.class.isInstance(value);
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

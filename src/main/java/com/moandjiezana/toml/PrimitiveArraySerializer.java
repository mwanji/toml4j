package com.moandjiezana.toml;

import java.util.Collection;

class PrimitiveArraySerializer extends ArraySerializer {
  static final Serializer PRIMITIVE_ARRAY_SERIALIZER = new PrimitiveArraySerializer();

  @Override
  public boolean canSerialize(Object value) {
    return isArrayish(value) && isArrayOfPrimitive(value);
  }

  @Override
  public void serialize(Object value, SerializerContext context) {
    Collection values = normalize(value);

    context.serialized.append("[ ");
    boolean first = true;
    for (Object elem : values) {
      if (!first) {
        context.serialized.append(", ");
      }
      Serializers.serialize(elem, context);
      first = false;
    }
    context.serialized.append(" ]");
  }
}

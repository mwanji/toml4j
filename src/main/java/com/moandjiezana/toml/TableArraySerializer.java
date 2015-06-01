package com.moandjiezana.toml;

import java.util.Collection;

class TableArraySerializer extends ArraySerializer {
  static final Serializer TABLE_ARRAY_SERIALIZER = new TableArraySerializer();

  @Override
  public boolean canSerialize(Object value) {
    return isArrayish(value) && !isArrayOfPrimitive(value);
  }

  @Override
  public void serialize(Object value, SerializerContext context) {
    Collection values = normalize(value);

    SerializerContext subContext = context.extend().setIsArrayOfTable(true);

    for (Object elem : values) {
      Serializers.serialize(elem, subContext);
    }
  }
}

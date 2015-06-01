package com.moandjiezana.toml;

import static com.moandjiezana.toml.BooleanSerializer.BOOLEAN_SERIALIZER;
import static com.moandjiezana.toml.DateSerializer.DATE_SERIALIZER;
import static com.moandjiezana.toml.MapSerializer.MAP_SERIALIZER;
import static com.moandjiezana.toml.NumberSerializer.NUMBER_SERIALIZER;
import static com.moandjiezana.toml.ObjectSerializer.OBJECT_SERIALIZER;
import static com.moandjiezana.toml.PrimitiveArraySerializer.PRIMITIVE_ARRAY_SERIALIZER;
import static com.moandjiezana.toml.StringSerializer.STRING_SERIALIZER;
import static com.moandjiezana.toml.TableArraySerializer.TABLE_ARRAY_SERIALIZER;

abstract class Serializers {
  private static final Serializer[] SERIALIZERS = {
      STRING_SERIALIZER, NUMBER_SERIALIZER, BOOLEAN_SERIALIZER, DATE_SERIALIZER,
      MAP_SERIALIZER, PRIMITIVE_ARRAY_SERIALIZER, TABLE_ARRAY_SERIALIZER
  };

  static Serializer findSerializerFor(Object value) {
    for (Serializer serializer : SERIALIZERS) {
      if (serializer.canSerialize(value)) {
        return serializer;
      }
    }

    return OBJECT_SERIALIZER;
  }

  static String serialize(Object value) {
    SerializerContext context = new SerializerContext();
    serialize(value, context);

    return context.serialized.toString();
  }

  static void serialize(Object value, SerializerContext context) {
    findSerializerFor(value).serialize(value, context);
  }
}

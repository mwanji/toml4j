package com.moandjiezana.toml;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

abstract class ArraySerializer implements Serializer {
  static protected boolean isArrayish(Object value) {
    return value instanceof Collection || value.getClass().isArray();
  }

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

  @Override
  public boolean isTable() {
    return false;
  }

  static boolean isArrayOfPrimitive(Object array) {
    Object first = peek(array);
    if (first != null) {
      Serializer serializer = Serializers.findSerializerFor(first);
      return serializer.isPrimitiveType() || isArrayish(first);
    }

    return false;
  }

  @SuppressWarnings("unchecked")
  protected Collection normalize(Object value) {
    Collection collection;

    if (value.getClass().isArray()) {
      // Arrays.asList() interprets an array as a single element,
      // so convert it to a list by hand
      collection = new ArrayList<Object>(Array.getLength(value));
      for (int i = 0; i < Array.getLength(value); i++) {
        Object elem = Array.get(value, i);
        collection.add(elem);
      }
    } else {
      collection = (Collection) value;
    }

    return collection;
  }

  @SuppressWarnings("unchecked")
  private static Object peek(Object value) {
    if (value.getClass().isArray()) {
      if (Array.getLength(value) > 0) {
        return Array.get(value, 0);
      } else {
        return null;
      }
    } else {
      Collection collection = (Collection) value;
      if (collection.size() > 0) {
        return collection.iterator().next();
      }
    }

    return null;
  }
}
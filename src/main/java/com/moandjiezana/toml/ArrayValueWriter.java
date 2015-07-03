package com.moandjiezana.toml;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import static com.moandjiezana.toml.ValueWriters.WRITERS;

abstract class ArrayValueWriter implements ValueWriter {
  static protected boolean isArrayish(Object value) {
    return value instanceof Collection || value.getClass().isArray();
  }

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

  static boolean isArrayOfPrimitive(Object array) {
    Object first = peek(array);
    if (first != null) {
      ValueWriter valueWriter = WRITERS.findWriterFor(first);
      return valueWriter.isPrimitiveType() || isArrayish(first);
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  protected Collection<?> normalize(Object value) {
    Collection<Object> collection;

    if (value.getClass().isArray()) {
      // Arrays.asList() interprets an array as a single element,
      // so convert it to a list by hand
      collection = new ArrayList<Object>(Array.getLength(value));
      for (int i = 0; i < Array.getLength(value); i++) {
        Object elem = Array.get(value, i);
        collection.add(elem);
      }
    } else {
      collection = (Collection<Object>) value;
    }

    return collection;
  }

  private static Object peek(Object value) {
    if (value.getClass().isArray()) {
      if (Array.getLength(value) > 0) {
        return Array.get(value, 0);
      } else {
        return null;
      }
    } else {
      Collection<?> collection = (Collection<?>) value;
      if (collection.size() > 0) {
        return collection.iterator().next();
      }
    }

    return null;
  }
}

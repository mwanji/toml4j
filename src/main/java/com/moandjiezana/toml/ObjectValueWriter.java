package com.moandjiezana.toml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.moandjiezana.toml.MapValueWriter.MAP_VALUE_WRITER;

class ObjectValueWriter implements ValueWriter {
  static final ValueWriter OBJECT_VALUE_WRITER = new ObjectValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return true;
  }

  @Override
  public void write(Object value, WriterContext context) {
    Map<String, Object> to = new LinkedHashMap<String, Object>();
    Set<Field> fields = getFieldsForClass(value.getClass());
    for (Field field : fields) {
      to.put(field.getName(), getFieldValue(field, value));
    }

    MAP_VALUE_WRITER.write(to, context);
  }

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

  @Override
  public boolean isTable() {
    return true;
  }

  static private Set<Field> getFieldsForClass(Class cls) {
    Set<Field> fields = new LinkedHashSet<Field>(Arrays.asList(cls.getDeclaredFields()));

    getSuperClassFields(cls.getSuperclass(), fields);

    // Skip final fields
    Set<Field> prunedFields = new LinkedHashSet<Field>();
    for (Field field : fields) {
      if (!Modifier.isFinal(field.getModifiers())) {
        prunedFields.add(field);
      }
    }

    return prunedFields;
  }

  static private void getSuperClassFields(Class cls, Set<Field> fields) {
    if (cls == Object.class) {
      return;
    }

    fields.addAll(Arrays.asList(cls.getDeclaredFields()));
    getSuperClassFields(cls.getSuperclass(), fields);
  }

  static private Object getFieldValue(Field field, Object o) {
    boolean isAccessible = field.isAccessible();
    field.setAccessible(true);
    Object value = null;
    try {
      value = field.get(o);
    } catch (IllegalAccessException ignored) {
    }
    field.setAccessible(isAccessible);

    return value;
  }

  private ObjectValueWriter() {}
}

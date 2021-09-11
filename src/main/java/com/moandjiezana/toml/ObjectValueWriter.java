package com.moandjiezana.toml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.moandjiezana.toml.MapValueWriter.MAP_VALUE_WRITER;
import static com.moandjiezana.toml.ValueWriters.WRITERS;

class ObjectValueWriter implements ValueWriter {
  static final ValueWriter OBJECT_VALUE_WRITER = new ObjectValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return true;
  }

  @Override
  public void write(Object value, WriterContext context) {
    write(value, context, null);
  }

  private ObjectValueWriter() {
  }

  private static Set<Field> getFields(Class<?> cls) {
    Set<Field> fields = new LinkedHashSet<>(Arrays.asList(cls.getDeclaredFields()));
    while (cls != Object.class) {
      fields.addAll(Arrays.asList(cls.getDeclaredFields()));
      cls = cls.getSuperclass();
    }
    removeConstantsAndSyntheticFields(fields);

    return fields;
  }

  private static void removeConstantsAndSyntheticFields(Set<Field> fields) {
    fields.removeIf(field -> (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) || field.isSynthetic() || Modifier.isTransient(field.getModifiers()) || field.isAnnotationPresent(TomlIgnore.class));
  }

  private static Object getFieldValue(Field field, Object o) {
    if (field.isAnnotationPresent(TomlIgnore.class)) return null;
    //noinspection deprecation
    boolean isAccessible = field.isAccessible();

    if (!field.trySetAccessible()) return null;
    Object value = null;
    try {
      value = field.get(o);
    } catch (IllegalAccessException ignored) {
    }
    field.setAccessible(isAccessible);
    return value;
  }

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

  public void write(Object value, WriterContext context, String[] objectComment) {
    final Map<String, Object> to = new LinkedHashMap<>();
    final Set<Field> fields = getFields(value.getClass());

    final ArrayList<String[]> comments = new ArrayList<>();
    final ArrayList<String[]> objComments = new ArrayList<>();

    for (Field field : fields) {
      final Object fieldValue = getFieldValue(field, value);
      to.put(field.getName(), fieldValue);
      final ValueWriter valueWriter = WRITERS.findWriterFor(fieldValue);
      if (field.isAnnotationPresent(TomlComment.class)) {
        for (Annotation a : field.getAnnotations()) {
          if (a instanceof TomlComment) {
            TomlComment comment = (TomlComment) a;
            if (valueWriter == OBJECT_VALUE_WRITER)
              objComments.add(comment.value());
            else
              comments.add(comment.value());
            break;
          }
        }
      } else {
        if (valueWriter == OBJECT_VALUE_WRITER)
          objComments.add(null);
        else
          comments.add(null);
      }
    }
    ((MapValueWriter) MAP_VALUE_WRITER).write(to, context, comments, objComments, objectComment);
  }
}

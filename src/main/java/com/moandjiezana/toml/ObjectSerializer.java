package com.moandjiezana.toml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

import static com.moandjiezana.toml.MapSerializer.MAP_SERIALIZER;

class ObjectSerializer implements Serializer {
    static final Serializer OBJECT_SERIALIZER = new ObjectSerializer();

    @Override
    public boolean canSerialize(Object value) {
        return true;
    }

    @Override
    public void serialize(Object value, SerializerContext context) {
        Map<String, Object> to = new LinkedHashMap<String, Object>();
        Set<Field> fields = getFieldsForClass(value.getClass());
        for (Field field : fields) {
            to.put(field.getName(), getFieldValue(field, value));
        }

        MAP_SERIALIZER.serialize(to, context);
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
        fields.removeIf(new Predicate<Field>() {
            @Override
            public boolean test(Field field) {
                return Modifier.isFinal(field.getModifiers());
            }
        });

        return fields;
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
}

package com.moandjiezana.toml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BurntSushiValidTest {

  
  @Test
  public void array_empty() throws Exception {
    run("array-empty");
  }
  
  @Test
  public void array_nospaces() throws Exception {
    run("array-nospaces");
  }
  
  @Test
  public void arrays_hetergeneous() throws Exception {
    run("arrays-hetergeneous");
  }
  
  @Test
  public void arrays_nested() throws Exception {
    run("arrays-nested");
  }
  
  @Test
  public void arrays() throws Exception {
    run("arrays");
  }
  
  @Test
  public void bool() throws Exception {
    run("bool");
  }

  @Test
  public void comments_everywhere() throws Exception {
    run("comments-everywhere");
  }
  
  @Test
  public void datetime() throws Exception {
    run("datetime");
  }
  
  @Test
  public void empty() throws Exception {
    run("empty");
  }
  
  @Test
  public void example() throws Exception {
    run("example");
  }
  
  @Test
  public void float_() throws Exception {
    run("float");
  }
  
  @Test
  public void implicit_and_explicit_after() throws Exception {
    run("implicit-and-explicit-after");
  }
  
  @Test
  public void implicit_and_explicit_before() throws Exception {
    run("implicit-and-explicit-before");
  }
  
  @Test
  public void implicit_groups() throws Exception {
    run("implicit-groups");
  }
  
  @Test
  public void integer() throws Exception {
    run("integer");
  }
  
  @Test
  public void key_equals_nospace() throws Exception {
    run("key-equals-nospace");
  }
  
  @Test
  public void key_space() throws Exception {
    run("key-space");
  }
  
  @Test
  public void key_special_chars() throws Exception {
    run("key-special-chars");
  }
  
  @Test
  public void keys_with_dots() throws Exception {
    run("keys-with-dots");
  }
  
  @Test
  public void long_float() throws Exception {
    run("long-float");
  }
  
  @Test
  public void long_integer() throws Exception {
    run("long-integer");
  }
  
  @Test
  public void multiline_string() throws Exception {
    run("multiline-string");
  }
  
  @Test
  public void raw_multiline_string() throws Exception {
    run("raw-multiline-string");
  }
  
  @Test
  public void raw_string() throws Exception {
    run("raw-string");
  }
  
  @Test
  public void string_empty() throws Exception {
    run("string-empty");
  }
  
  @Test
  public void string_escapes() throws Exception {
    run("string-escapes");
  }
  
  @Test
  public void string_simple() throws Exception {
    run("string-simple");
  }
  
  @Test
  public void string_with_pound() throws Exception {
    run("string-with-pound");
  }

  @Test
  public void table_array_implicit() throws Exception {
    run("table-array-implicit");
  }

  @Test
  public void table_array_many() throws Exception {
    run("table-array-many");
  }

  @Test
  public void table_array_nest() throws Exception {
    run("table-array-nest");
  }

  @Test
  public void table_array_one() throws Exception {
    run("table-array-one");
  }

  @Test
  public void table_empty() throws Exception {
    run("table-empty");
  }

  @Test
  public void table_sub_empty() throws Exception {
    run("table-sub-empty");
  }

  @Test @Ignore
  public void table_whitespace() throws Exception {
    run("table-whitespace");
  }

  @Test @Ignore
  public void table_with_pound() throws Exception {
    run("table-with-pound");
  }

  @Test
  public void unicode_escape() throws Exception {
    run("unicode-escape");
  }

  @Test
  public void unicode_literal() throws Exception {
    run("unicode-literal");
  }

  @After
  public void after() throws IOException {
    inputToml.close();
    if (expectedJsonReader != null) {
      expectedJsonReader.close();
    }
  }

  private void run(String testName) {
    inputToml = getClass().getResourceAsStream("burntsushi/valid/" + testName + ".toml");
    expectedJsonReader = new InputStreamReader(getClass().getResourceAsStream("burntsushi/valid/" + testName + ".json"));
    JsonElement expectedJson = new Gson().fromJson(expectedJsonReader, JsonElement.class);

    Toml toml = new Toml().parse(inputToml);
    JsonElement actual = toml.to(JsonElement.class, TEST_GSON);

    Assert.assertEquals(expectedJson, actual);
  }

  private InputStream inputToml;
  private InputStreamReader expectedJsonReader;

  private static final Gson TEST_GSON = new GsonBuilder()
    .registerTypeAdapter(Boolean.class, serialize(Boolean.class))
    .registerTypeAdapter(String.class, serialize(String.class))
    .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
      @Override
      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return context.serialize(new Value("datetime", iso8601Format.format(src)));
      }
    })
    .registerTypeHierarchyAdapter(Number.class, new JsonSerializer<Number>() {
      @Override
      public JsonElement serialize(Number src, Type typeOfSrc, JsonSerializationContext context) {
        String number = src.toString();
        String type = number.contains(".") ? "float" : "integer";

        return context.serialize(new Value(type, number));
      }
    })
    .registerTypeHierarchyAdapter(List.class, new JsonSerializer<List<?>>() {
      @Override
      public JsonElement serialize(List<?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Object o : src) {
          array.add(context.serialize(o));
        }

        if (!src.isEmpty() && src.get(0) instanceof Map) {
          return array;
        }

        JsonObject object = new JsonObject();
        object.add("type", new JsonPrimitive("array"));
        object.add("value", array);

        return object;
      }
    })
    .registerTypeAdapter(Value.class, new JsonSerializer<Value>() {
      @Override
      public JsonElement serialize(Value src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("type", new JsonPrimitive(src.type));
        object.add("value", new JsonPrimitive(src.value));

        return object;
      }
    })
    .create();

  private static class Value {
    public final String type;
    public final String value;

    public Value(String type, String value) {
      this.type = type;
      this.value = value;
    }
  }

  private static <T> JsonSerializer<T> serialize(final Class<T> aClass) {
    return new JsonSerializer<T>() {
      @Override
      public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(new Value(toTomlType(aClass), src.toString()));
      }
    };
  }

  private static String toTomlType(Class<?> aClass) {
    if (aClass == String.class) {
      return "string";
    }

    if (aClass == Float.class || aClass == Double.class) {
      return "float";
    }

    if (Number.class.isAssignableFrom(aClass)) {
      return "integer";
    }

    if (aClass == Date.class) {
      return "datetime";
    }

    if (aClass == Boolean.class) {
      return "bool";
    }

    return "array";
  }
}

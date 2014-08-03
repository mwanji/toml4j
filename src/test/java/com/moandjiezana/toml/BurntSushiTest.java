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

public class BurntSushiTest {

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

  @Test
  public void comments_everywhere() throws Exception {
    inputToml = getClass().getResourceAsStream("burntsushi/valid/comments_everywhere.toml");
    expectedJsonReader = new InputStreamReader(getClass().getResourceAsStream("burntsushi/valid/comments_everywhere.json"));
    JsonElement expectedJson = new Gson().fromJson(expectedJsonReader, JsonElement.class);

    JsonElement actual = new Toml().parse(inputToml).to(JsonElement.class, TEST_GSON);

    Assert.assertEquals(expectedJson, actual);
  }

  @Test
  @Ignore
  public void key_special_chars() throws Exception {
    runValidTest("key-special-chars");
  }

  @Test
  public void table_array_implicit() throws Exception {
    runValidTest("table-array-implicit");
  }

  @Test
  public void array_mixed_types_ints_and_floats() throws Exception {
    runInvalidTest("array-mixed-types-ints-and-floats");
  }

  @Test
  public void datetime_malformed_no_leads() throws Exception {
    runInvalidTest("datetime-malformed-no-leads");
  }

  @After
  public void after() throws IOException {
    inputToml.close();
    if (expectedJsonReader != null) {
      expectedJsonReader.close();
    }
  }

  private void runValidTest(String testName) {
    inputToml = getClass().getResourceAsStream("burntsushi/valid/" + testName + ".toml");
    expectedJsonReader = new InputStreamReader(getClass().getResourceAsStream("burntsushi/valid/" + testName + ".json"));
    JsonElement expectedJson = new Gson().fromJson(expectedJsonReader, JsonElement.class);

    Toml toml = new Toml().parse(inputToml);
    JsonElement actual = toml.to(JsonElement.class, TEST_GSON);

    Assert.assertEquals(expectedJson, actual);
  }

  private void runInvalidTest(String testName) {
    inputToml = getClass().getResourceAsStream("burntsushi/invalid/" + testName + ".toml");

    try {
      new Toml().parse(inputToml);
      Assert.fail("Should have rejected invalid input!");
    } catch (IllegalStateException e) {
      // success
    }
  }

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

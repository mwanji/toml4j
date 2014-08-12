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
  public void key_empty() throws Exception {
    runInvalidTest("key-empty");
  }
  
  @Test
  public void key_hash() throws Exception {
    runInvalidTest("key-hash");
  }
  
  @Test
  public void key_newline() throws Exception {
    runInvalidTest("key-newline");
  }
  
  @Test
  public void key_open_bracket() throws Exception {
    runInvalidTest("key-open-bracket");
  }
  
  @Test
  public void key_single_open_bracket() throws Exception {
    runInvalidTest("key-single-open-bracket");
  }
  
  @Test
  public void key_start_bracket() throws Exception {
    runInvalidTest("key-start-bracket");
  }
  
  @Test
  public void key_two_equals() throws Exception {
    runInvalidTest("key-two-equals");
  }

  @Test
  @Ignore
  public void key_special_chars() throws Exception {
    runValidTest("key-special-chars");
  }
  
  @Test
  public void string_bad_byte_escape() throws Exception {
    runInvalidTest("string-bad-byte-escape");
  }
  
  @Test
  public void string_bad_escape() throws Exception {
    runInvalidTest("string-bad-escape");
  }
  
  @Test
  public void string_byte_escapes() throws Exception {
    runInvalidTest("string-byte-escapes");
  }
  
  @Test
  public void string_no_close() throws Exception {
    runInvalidTest("string-no-close");
  }

  @Test
  public void table_array_implicit() throws Exception {
    runInvalidTest("table-array-implicit");
    runValidTest("table-array-implicit");
  }

  @Test
  public void table_array_malformed_bracket() throws Exception {
    runInvalidTest("table-array-malformed-bracket");
  }
  
  @Test
  public void table_array_malformed_empty() throws Exception {
    runInvalidTest("table-array-malformed-empty");
  }
  
  @Test
  public void table_empty() throws Exception {
    runInvalidTest("table-empty");
  }
  
  @Test
  public void table_nested_brackets_close() throws Exception {
    runInvalidTest("table-nested-brackets-close");
  }
  
  @Test
  public void table_nested_brackets_open() throws Exception {
    runInvalidTest("table-nested-brackets-open");
  }

  @Test
  public void empty_implicit_table() {
    runInvalidTest("empty-implicit-table");
  }

  @Test
  public void empty_table() throws Exception {
    runInvalidTest("empty-table");
  }

  @Test
  public void array_mixed_types_ints_and_floats() throws Exception {
    runInvalidTest("array-mixed-types-ints-and-floats");
  }

  @Test
  public void array_mixed_types_arrays_and_ints() throws Exception {
    runInvalidTest("array-mixed-types-arrays-and-ints");
  }
  
  @Test
  public void array_mixed_types_strings_and_ints() throws Exception {
    runInvalidTest("array-mixed-types-strings-and-ints");
  }
  
  @Test
  public void array_empty() throws Exception {
    runValidTest("array-empty");
  }
  
  @Test
  public void arrays_hetergeneous() throws Exception {
    runValidTest("arrays-hetergeneous");
  }

  @Test
  public void datetime_malformed_no_leads() throws Exception {
    runInvalidTest("datetime-malformed-no-leads");
  }

  @Test
  public void datetime_malformed_no_secs() throws Exception {
    runInvalidTest("datetime-malformed-no-secs");
  }

  @Test
  public void datetime_malformed_no_t() throws Exception {
    runInvalidTest("datetime-malformed-no-t");
  }

  @Test
  public void datetime_malformed_no_z() throws Exception {
    runInvalidTest("datetime-malformed-no-z");
  }

  @Test
  public void datetime_malformed_with_milli() throws Exception {
    runInvalidTest("datetime-malformed-with-milli");
  }
  
  @Test
  public void duplicate_key_table() throws Exception {
    runInvalidTest("duplicate-key-table");
  }
  
  @Test
  public void duplicate_keys() throws Exception {
    runInvalidTest("duplicate-keys");
  }
  
  @Test
  public void duplicate_tables() throws Exception {
    runInvalidTest("duplicate-tables");
  }

  @Test
  public void float_no_leading_zero() throws Exception {
    runInvalidTest("float-no-leading-zero");
  }
  
  @Test
  public void float_no_trailing_digits() throws Exception {
    runInvalidTest("float-no-trailing-digits");
  }

  @Test
  public void text_after_array_entries() throws Exception {
    runInvalidTest("text-after-array-entries");
  }

  @Test
  public void text_after_integer() throws Exception {
    runInvalidTest("text-after-integer");
  }

  @Test
  public void text_after_string() throws Exception {
    runInvalidTest("text-after-string");
  }

  @Test
  public void text_after_table() throws Exception {
    runInvalidTest("text-after-table");
  }

  @Test
  public void text_before_array_separator() throws Exception {
    runInvalidTest("text-before-array-separator");
  }

  @Test
  public void text_in_array() throws Exception {
    runInvalidTest("text-in-array");
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

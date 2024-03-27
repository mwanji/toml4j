package com.moandjiezana.toml.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.ExtraPrimitives;
import com.moandjiezana.toml.FruitArray;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlPrimitives;
import com.moandjiezana.toml.TomlTableArrays;
import com.moandjiezana.toml.TomlTables;
import com.moandjiezana.toml.FruitArray.Fruit;

public class TomlToClassTest {

  @Test
  public void should_convert_toml_primitives() throws Exception {
    Toml toml = new Toml().read(file("should_convert_primitive_values.toml"));

    TomlPrimitives values = toml.to(TomlPrimitives.class);

    Calendar calendar = Calendar.getInstance();
    calendar.set(1979, Calendar.MAY, 27, 7, 32, 00);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

    assertEquals("string", values.string);
    assertEquals((Long) 123L, values.number);
    assertEquals(2.1, values.decimal, 0);
    assertTrue(values.bool);
    assertEquals(calendar.getTime(), values.date);
  }

  @Test
  public void should_convert_to_non_toml_primitives() throws Exception {
    ExtraPrimitives extraPrimitives = new Toml().read(file("should_convert_extra_primitives.toml")).to(ExtraPrimitives.class);

    assertEquals("value", extraPrimitives.group.get("key"), "Did not convert table to map");
    assertEquals(BigDecimal.valueOf(1.2), extraPrimitives.bigDecimal, "Did not convert double to BigDecimal");
    assertEquals(BigInteger.valueOf(5), extraPrimitives.bigInteger, "Did not convert integer to BigInteger");
    assertEquals(Short.parseShort("3"), extraPrimitives.aShort, "Did not convert integer to short");
    assertEquals(Integer.valueOf(7), extraPrimitives.anInteger, "Did not convert integer to Integer");
    assertEquals(Character.valueOf('u'), extraPrimitives.character, "Did not convert string to Character");
    assertEquals(new URL("http://www.example.com").toString(), extraPrimitives.url.toString(), "Did not convert string to URL");
    assertEquals(new URI("http://www.test.com").toString(), extraPrimitives.uri.toString(), "Did not convert string to URI");
    assertThat(extraPrimitives.set, contains("a", "b"));
    assertThat(extraPrimitives.strings, arrayContaining("c", "d"));
    assertEquals(ElementType.CONSTRUCTOR, extraPrimitives.elementType, "Did not convert string to enum");
  }

  @Test
  public void should_convert_tables() throws Exception {
    String fileName = "should_convert_tables.toml";
    Toml toml = new Toml().read(file(fileName));

    TomlTables tomlTables = toml.to(TomlTables.class);

    assertEquals("value1", tomlTables.group1.string);
    assertEquals("value2", tomlTables.group2.string);
  }

  @Test
  public void should_convert_tables_with_defaults() throws Exception {
    Toml defaultToml = new Toml().read("[group2]\n string=\"defaultValue2\"\n number=2\n [group3]\n string=\"defaultValue3\"");
    Toml toml = new Toml(defaultToml).read(file("should_convert_tables.toml"));

    TomlTables tomlTables = toml.to(TomlTables.class);

    assertEquals("value1", tomlTables.group1.string);
    assertEquals("value2", tomlTables.group2.string);
    assertNull(tomlTables.group2.number);
    assertEquals("defaultValue3", tomlTables.group3.string);
  }

  @Test
  public void should_use_defaults() throws Exception {
    Toml defaults = new Toml().read(file("should_convert_tables.toml"));
    Toml toml = new Toml(defaults).read("");

    TomlTables tomlTables = toml.to(TomlTables.class);

    assertEquals("value1", tomlTables.group1.string);
    assertEquals("value2", tomlTables.group2.string);
  }

  @Test
  public void should_ignore_keys_not_in_class() throws Exception {
    TomlPrimitives tomlPrimitives = new Toml().read("a=1\nstring=\"s\"").to(TomlPrimitives.class);

    assertEquals("s", tomlPrimitives.string);
  }

  @Test
  public void should_convert_table_array() throws Exception {
    TomlTableArrays toml = new Toml().read(file("should_convert_table_array_to_class.toml")).to(TomlTableArrays.class);

    assertEquals(2, toml.groupers.size());
    assertEquals("grouper 1", toml.groupers.get(0).string);
    assertEquals("grouper 2", toml.groupers.get(1).string);

    assertEquals("My Name", toml.name);
    assertEquals(12, toml.primitives.number.intValue());
  }

  @Test
  public void should_convert_fruit_table_array() throws Exception {
    FruitArray fruitArray = new Toml().read(file("fruit_table_array.toml")).to(FruitArray.class);

    assertEquals(2, fruitArray.fruit.size());
    Fruit apple = fruitArray.fruit.get(0);
    assertEquals("apple", apple.name);
    assertEquals("red", apple.physical.color);
    assertEquals("round", apple.physical.shape);
    assertEquals(2, apple.variety.size());
    assertEquals("red delicious", apple.variety.get(0).get("name"));
    assertEquals("granny smith", apple.variety.get(1).get("name"));

    Fruit banana = fruitArray.fruit.get(1);
    assertEquals("banana", banana.name);
    assertEquals(1, banana.variety.size());
    assertEquals("plantain", banana.variety.get(0).get("name"));
  }

  private File file(String fileName) {
    return new File(getClass().getResource(fileName).getFile());
  }
}

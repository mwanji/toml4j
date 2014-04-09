package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.TimeZone;

import org.junit.Test;

import com.moandjiezana.toml.testutils.ExtraPrimitives;
import com.moandjiezana.toml.testutils.FruitArray;
import com.moandjiezana.toml.testutils.FruitArray.Fruit;
import com.moandjiezana.toml.testutils.TomlPrimitives;
import com.moandjiezana.toml.testutils.TomlTableArrays;
import com.moandjiezana.toml.testutils.TomlTables;

public class TomlToClassTest {

  @Test
  public void should_convert_toml_primitives() throws Exception {
    Toml toml = new Toml().parse(file("should_convert_primitive_values.toml"));

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
    ExtraPrimitives extraPrimitives = new Toml().parse(file("should_convert_extra_primitives.toml")).to(ExtraPrimitives.class);

    assertEquals("Did not convert table to map", "value", extraPrimitives.group.get("key"));
    assertEquals("Did not convert double to BigDecimal", BigDecimal.valueOf(1.2), extraPrimitives.bigDecimal);
    assertEquals("Did not convert integer to BigInteger", BigInteger.valueOf(5), extraPrimitives.bigInteger);
    assertEquals("Did not convert integer to short", Short.parseShort("3"), extraPrimitives.aShort);
    assertEquals("Did not convert integer to Integer", Integer.valueOf(7), extraPrimitives.anInteger);
    assertEquals("Did not convert string to Character", Character.valueOf('u'), extraPrimitives.character);
    assertEquals("Did not convert string to URL", new URL("http://www.example.com").toString(), extraPrimitives.url.toString());
    assertEquals("Did not convert string to URI", new URI("http://www.test.com").toString(), extraPrimitives.uri.toString());
    assertEquals("Did not convert list to Set", new HashSet<String>(Arrays.asList("a", "b")), extraPrimitives.set);
    assertEquals("Did not convert string to enum", ElementType.CONSTRUCTOR, extraPrimitives.elementType);
  }

  @Test
  public void should_convert_tables() throws Exception {
    String fileName = "should_convert_tables.toml";
    Toml toml = new Toml().parse(file(fileName));

    TomlTables tomlTables = toml.to(TomlTables.class);

    assertEquals("value1", tomlTables.group1.string);
    assertEquals("value2", tomlTables.group2.string);
  }

  @Test
  public void should_use_defaults() throws Exception {
    Toml defaults = new Toml().parse(file("should_convert_tables.toml"));
    Toml toml = new Toml(defaults).parse("");

    TomlTables tomlTables = toml.to(TomlTables.class);

    assertEquals("value1", tomlTables.group1.string);
    assertEquals("value2", tomlTables.group2.string);
  }

  @Test
  public void should_ignore_keys_not_in_class() throws Exception {
    TomlPrimitives tomlPrimitives = new Toml().parse("a=1\nstring=\"s\"").to(TomlPrimitives.class);

    assertEquals("s", tomlPrimitives.string);
  }

  @Test
  public void should_convert_table_array() throws Exception {
    TomlTableArrays toml = new Toml().parse(file("should_convert_table_array_to_class.toml")).to(TomlTableArrays.class);

    assertEquals(2, toml.groupers.size());
    assertEquals("grouper 1", toml.groupers.get(0).string);
    assertEquals("grouper 2", toml.groupers.get(1).string);

    assertEquals("My Name", toml.name);
    assertEquals(12, toml.primitives.number.intValue());
  }

  @Test
  public void should_convert_fruit_table_array() throws Exception {
    FruitArray fruitArray = new Toml().parse(file("fruit_table_array.toml")).to(FruitArray.class);

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

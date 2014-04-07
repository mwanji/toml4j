package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

import com.moandjiezana.toml.testutils.TableAsMap;
import com.moandjiezana.toml.testutils.TomlPrimitives;
import com.moandjiezana.toml.testutils.TomlTableArrays;
import com.moandjiezana.toml.testutils.TomlTables;

public class TomlToClassTest {

  @Test
  public void should_convert_primitive_values() throws Exception {
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
  public void should_convert_table_as_map() throws Exception {
    TableAsMap tableAsMap = new Toml().parse("[group]\nkey=\"value\"").to(TableAsMap.class);

    assertEquals("value", tableAsMap.group.get("key"));
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

  private File file(String fileName) {
    return new File(getClass().getResource(fileName).getFile());
  }
}

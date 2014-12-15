package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.fest.reflect.core.Reflection;
import org.hamcrest.Matchers;
import org.junit.Test;

public class TomlTest {
  @Test
  public void should_get_table() throws Exception {
    Toml toml = new Toml().parse("[group]\nkey = \"value\"");

    Toml group = toml.getTable("group");

    assertEquals("value", group.getString("key"));
  }

  @Test
  public void should_get_value_for_multi_key() throws Exception {
    Toml toml = new Toml().parse("[group]\nkey = \"value\"");

    assertEquals("value", toml.getString("group.key"));
  }

  @Test
  public void should_get_value_for_multi_key_with_no_parent_table() throws Exception {
    Toml toml = new Toml().parse("[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getString("group.sub.key"));
  }

  @Test
  public void should_get_table_for_multi_key() throws Exception {
    Toml toml = new Toml().parse("[group]\nother=1\n[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getTable("group.sub").getString("key"));
  }

  @Test
  public void should_get_table_for_multi_key_with_no_parent_table() throws Exception {
    Toml toml = new Toml().parse("[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getTable("group.sub").getString("key"));
  }

  @Test
  public void should_get_value_from_table_with_sub_table() throws Exception {
    Toml toml = new Toml().parse("[a.b]\nc=1\n[a]\nd=2");

    assertEquals(2, toml.getLong("a.d").intValue());
    assertEquals(1, toml.getTable("a.b").getLong("c").intValue());
  }

  @Test
  public void should_return_null_if_no_value_for_key() throws Exception {
    Toml toml = new Toml().parse("");

    assertNull(toml.getString("a"));
  }

  @Test
  public void should_return_empty_list_if_no_value_for_key() throws Exception {
    Toml toml = new Toml().parse("");

    assertTrue(toml.getList("a", String.class).isEmpty());
  }

  @Test
  public void should_return_null_when_no_value_for_multi_key() throws Exception {
    Toml toml = new Toml().parse("");

    assertNull(toml.getString("group.key"));
  }

  @Test
  public void should_return_empty_toml_when_no_value_for_table() throws Exception {
    Toml toml = new Toml().parse("[a]").getTable("b");

    assertTrue(Reflection.field("values").ofType(Map.class).in(toml).get().isEmpty());
    assertNull(toml.getString("x"));
  }

  @Test
  public void should_load_from_file() throws Exception {
    Toml toml = new Toml().parse(new File(getClass().getResource("should_load_from_file.toml").getFile()));

    assertEquals("value", toml.getString("key"));
  }

  @Test
  public void should_support_numbers_in_key_names() throws Exception {
    Toml toml = new Toml().parse("a1 = 1");

    assertEquals(1, toml.getLong("a1").intValue());
  }

  @Test
  public void should_support_numbers_in_table_names() throws Exception {
    Toml toml = new Toml().parse("[group1]\na = 1");

    assertEquals(1, toml.getLong("group1.a").intValue());
  }

  @Test
  public void should_support_underscores_in_key_names() throws Exception {
    Toml toml = new Toml().parse("a_a = 1");

    assertEquals(1, toml.getLong("a_a").intValue());
  }

  @Test
  public void should_support_question_marks_in_key_names() throws Exception {
    Toml toml = new Toml().parse("key?=true");

    assertTrue(toml.getBoolean("key?"));
  }
  
  @Test
  public void should_support_dots_in_key_names() throws Exception {
    Toml toml = new Toml().parse(file("should_support_dots_in_key_names"));
    
    assertEquals(1, toml.getLong("a").intValue());
    assertEquals(2, toml.getLong("b.c").intValue());
    assertEquals(3, toml.getTable("b").getLong("c").intValue());
    assertEquals(4, toml.getLong("b.a.b").intValue());
    assertEquals(5, toml.getLong("d.e.a").intValue());
    assertEquals(6, toml.getLong("d.e.a.b.c").intValue());
    assertEquals(6, toml.getTable("d.e").getLong("a.b.c").intValue());
    assertEquals(7, toml.getTables("f").get(0).getLong("a.b").intValue());
    assertEquals(8, toml.getLong("f[1].a.b").intValue());
  }

  @Test
  public void should_support_underscores_in_table_names() throws Exception {
    Toml toml = new Toml().parse("[group_a]\na = 1");

    assertEquals(1, toml.getLong("group_a.a").intValue());
  }

  @Test
  public void should_support_sharp_sign_in_table_names() throws Exception {
    Toml toml = new Toml().parse("[group#]\nkey=1");

    assertEquals(1, toml.getLong("group#.key").intValue());
  }
  
  @Test
  public void should_support_spaces_in_table_names() throws Exception {
    Toml toml = new Toml().parse("[valid  key]");
    
    assertNotNull(toml.getTable("valid  key"));
  }

  @Test
  public void should_support_blank_lines() throws Exception {
    Toml toml = new Toml().parse(new File(getClass().getResource("should_support_blank_line.toml").getFile()));

    assertEquals(1, toml.getLong("group.key").intValue());
  }

  @Test
  public void should_allow_comment_after_values() throws Exception {
    Toml toml = new Toml().parse(new File(getClass().getResource("should_allow_comment_after_values.toml").getFile()));

    assertEquals(1, toml.getLong("a").intValue());
    assertEquals(1.1, toml.getDouble("b").doubleValue(), 0);
    assertEquals("abc", toml.getString("c"));
    Calendar cal = Calendar.getInstance();
    cal.set(2014, Calendar.AUGUST, 4, 13, 47, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    assertEquals(cal.getTime(), toml.getDate("d"));
    assertThat(toml.getList("e", String.class), Matchers.contains("a", "b"));
    assertTrue(toml.getBoolean("f"));
    assertEquals("abc", toml.getString("g"));
    assertEquals("abc", toml.getString("h"));
    assertEquals("abc\nabc", toml.getString("i"));
    assertEquals("abc\nabc", toml.getString("j"));
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_empty_key_name() throws Exception {
    new Toml().parse(" = 1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_key_name_with_hash() throws Exception {
    new Toml().parse("a# = 1");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_key_is_overwritten_by_table() {
    new Toml().parse("[a]\nb=1\n[a.b]\nc=2");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_key_is_overwritten_by_another_key() {
    new Toml().parse("[fruit]\ntype=\"apple\"\ntype=\"orange\"");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_table_defined_twice() throws Exception {
    new Toml().parse("[a]\nb=1\n[a]\nc=2");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_illegal_characters_after_table() throws Exception {
    new Toml().parse("[error]   if you didn't catch this, your parser is broken");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_empty_table_name() {
    new Toml().parse("[]");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_compound_table_name_ending_with_empty_table_name() {
    new Toml().parse("[a.]");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_compound_table_name_containing_empty_table_name() {
    new Toml().parse("[a..b]");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_compound_table_name_starting_with_empty_table_name() {
    new Toml().parse("[.b]");
  }

  private File file(String file) {
    return new File(getClass().getResource(file + ".toml").getFile());
  }
}

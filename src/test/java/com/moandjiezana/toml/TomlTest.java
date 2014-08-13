package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.fest.reflect.core.Reflection;
import org.hamcrest.Matchers;
import org.junit.Test;

public class TomlTest {

  @Test
  public void should_get_string() throws Exception {
    Toml toml = new Toml().parse("a = \"a\"");

    assertEquals("a", toml.getString("a"));
  }

  @Test
  public void should_get_empty_string() {
    Toml toml = new Toml().parse("a = \"\"");
    assertEquals("", toml.getString("a"));
  }

  @Test
  public void should_get_empty_string_with_trailing_new_line() {
    Toml toml = new Toml().parse("a = \"\"\n");
    assertEquals("", toml.getString("a"));
  }
  
  @Test
  public void should_get_basic_multiline_string() throws Exception {
    Toml toml = new Toml().parse(file("should_get_basic_multiline_string"));
    
    assertEquals(toml.getString("ref"), toml.getString("one_line"));
    assertEquals(toml.getString("ref"), toml.getString("many_lines"));
  }
  
  @Test
  public void should_get_multiline_string_without_new_lines() throws Exception {
    Toml toml = new Toml().parse(file("should_get_multiline_string_without_new_lines"));
    
    assertEquals(toml.getString("ref"), toml.getString("multi1"));
    assertEquals(toml.getString("ref"), toml.getString("multi2"));
  }
  
  @Test
  public void should_get_literal_string() throws Exception {
    Toml toml = new Toml().parse(file("should_get_literal_string"));
    
    assertEquals("C:\\Users\\nodejs\\templates", toml.getString("winpath"));
    assertEquals("\\\\ServerX\\admin$\\system32\\", toml.getString("winpath2"));
    assertEquals("Tom \"Dubs\" Preston-Werner", toml.getString("quoted"));
    assertEquals("<\\i\\c*\\s*>", toml.getString("regex"));
  }

  @Test
  public void should_get_number() throws Exception {
    Toml toml = new Toml().parse("b = 1001");

    assertEquals(1001, toml.getLong("b").intValue());
  }

  @Test
  public void should_get_negative_number() throws Exception {
    Toml toml = new Toml().parse("b = -1001");

    assertEquals(-1001, toml.getLong("b").intValue());
  }

  @Test
  public void should_get_array() throws Exception {
    Toml toml = new Toml().parse("list = [\"a\", \"b\", \"c\"]");

    assertEquals(asList("a", "b", "c"), toml.getList("list", String.class));
  }

  @Test
  public void should_allow_multiline_array() throws Exception {
    Toml toml = new Toml().parse(file("should_allow_multiline_array"));

    assertEquals(asList("a", "b", "c"), toml.getList("a", String.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void should_get_nested_arrays() throws Exception {
    Toml clients = new Toml().parse("data = [ [\"gamma\", \"delta\"], [1, 2]] # just an update to make sure parsers support it");

    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.getList("data", String.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void should_get_nested_arrays_with_no_space_between_outer_and_inner_array() throws Exception {
    Toml clients = new Toml().parse("data = [[\"gamma\", \"delta\"], [1, 2]] # just an update to make sure parsers support it");

    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.getList("data", String.class));
  }

  @Test
  public void should_get_boolean() throws Exception {
    Toml toml = new Toml().parse("bool_false = false\nbool_true = true");

    assertFalse(toml.getBoolean("bool_false"));
    assertTrue(toml.getBoolean("bool_true"));
  }

  @Test
  public void should_get_date() throws Exception {
    Toml toml = new Toml().parse("a_date = 2011-11-10T13:12:00Z");

    Calendar calendar = Calendar.getInstance();
    calendar.set(2011, Calendar.NOVEMBER, 10, 13, 12, 00);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }

  @Test
  public void should_get_double() throws Exception {
    Toml toml = new Toml().parse("double = 5.25");

    assertEquals(5.25D, toml.getDouble("double").doubleValue(), 0.0);
  }

  @Test
  public void should_get_negative_double() throws Exception {
    Toml toml = new Toml().parse("double = -5.25");

    assertEquals(-5.25D, toml.getDouble("double").doubleValue(), 0.0);
  }

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
  public void should_return_empty_list_when_no_value_for_table_array() throws Exception {
    List<Toml> tomls = new Toml().parse("[a]").getTables("b");

    assertTrue(tomls.isEmpty());
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
  }

  @Test
  public void should_support_special_characters_in_strings() {
    Toml toml = new Toml().parse(new File(getClass().getResource("should_support_special_characters_in_strings.toml").getFile()));

    assertEquals("\" \t \n \r \\ / \b \f", toml.getString("key"));
  }

  @Test
  public void should_support_unicode_characters_in_strings() throws Exception {
    Toml toml = new Toml().parse(new File(getClass().getResource("should_support_special_characters_in_strings.toml").getFile()));

    assertEquals("more or less ±", toml.getString("unicode_key"));
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_reserved_special_character_in_strings() throws Exception {
    new Toml().parse("key=\"\\m\"");
  }

  @Test
  public void should_ignore_comma_at_end_of_array() throws Exception {
    Toml toml = new Toml().parse("key=[1,2,3,]");

    assertEquals(asList(1L, 2L, 3L), toml.getList("key", Long.class));
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
  public void should_fail_on_non_existant_date() throws Exception {
    new Toml().parse("d = 2012-13-01T15:00:00Z");
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
  public void should_fail_on_invalid_number() throws Exception {
    new Toml().parse("a = 200-");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_illegal_characters_after_table() throws Exception {
    new Toml().parse("[error]   if you didn't catch this, your parser is broken");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_when_illegal_characters_after_key() throws Exception {
    new Toml().parse("number = 3.14  pi");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_when_illegal_characters_after_integer() throws Exception {
    new Toml().parse("number = 314  pi");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_float_without_leading_0() {
    new Toml().parse("answer = .12345");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_negative_float_without_leading_0() {
    new Toml().parse("answer = -.12345");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_float_without_digits_after_dot() {
    new Toml().parse("answer = 1.");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_negative_float_without_digits_after_dot() {
    new Toml().parse("answer = -1.");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_boolean_true() {
    new Toml().parse("answer = true abc");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_boolean_false() {
    new Toml().parse("answer = false abc");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_literal_string() {
    new Toml().parse("a = ' ' jdkf");
  }
  
  private File file(String file) {
    return new File(getClass().getResource(file + ".toml").getFile());
  }
}

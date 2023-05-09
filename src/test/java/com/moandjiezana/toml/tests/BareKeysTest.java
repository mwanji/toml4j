package com.moandjiezana.toml.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;

public class BareKeysTest {
  
  @Test
  public void should_ignore_spaces_around_key_segments() throws Exception {
    Toml toml = new Toml().read("[ a . b   . c  ]  \n  key = \"a\"");
    
    assertEquals("a", toml.getString("a.b.c.key"));
  }

  @Test
  public void should_support_underscores_in_key_names() throws Exception {
    Toml toml = new Toml().read("a_a = 1");

    assertEquals(1, toml.getLong("a_a").intValue());
  }

  @Test
  public void should_support_underscores_in_table_names() throws Exception {
    Toml toml = new Toml().read("[group_a]\na = 1");

    assertEquals(1, toml.getLong("group_a.a").intValue());
  }

  @Test
  public void should_support_numbers_in_key_names() throws Exception {
    Toml toml = new Toml().read("a1 = 1");

    assertEquals(1, toml.getLong("a1").intValue());
  }

  @Test
  public void should_support_numbers_in_table_names() throws Exception {
    Toml toml = new Toml().read("[group1]\na = 1");

    assertEquals(1, toml.getLong("group1.a").intValue());
  }

  @Test
  public void should_fail_when_characters_outside_accept_range_are_used_in_table_name() throws Exception {
    assertThrows(IllegalStateException.class, () -> new Toml().read("[~]"));
  }

  @Test
  public void should_fail_when_characters_outside_accept_range_are_used_in_table_array_name() throws Exception {
    assertThrows(IllegalStateException.class, () -> new Toml().read("[[~]]"));
  }
  
  @Test
  public void should_fail_when_dots_in_key_name() throws Exception {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("a.b = 1"));
  }

  @Test
  public void should_fail_when_characters_outside_accept_range_are_used_in_key_name() throws Exception {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("~ = 1"));
  }

  @Test
  public void should_fail_on_sharp_sign_in_table_name() throws Exception {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[group#]\nkey=1"));
  }
  
  @Test
  public void should_fail_on_spaces_in_table_name() throws Exception {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[valid  key]"));
  }

  @Test
  public void should_fail_on_sharp_sign_in_table_array_name() throws Exception {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[[group#]]\nkey=1"));
  }
  
  @Test
  public void should_fail_on_spaces_in_table_array_name() throws Exception {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[[valid  key]]"));
  }

  @Test
  public void should_fail_on_question_marks_in_key_name() throws Exception {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("key?=true"));
  }
  
  @Test
  public void should_fail_on_empty_table_name() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[]"));
  }
  
  @Test
  public void should_fail_on_nested_table_name_ending_with_empty_table_name() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[a.]"));
  }
  
  @Test
  public void should_fail_on_nested_table_name_containing_empty_table_name() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[a..b]"));
  }
  
  @Test
  public void should_fail_on_nested_table_name_starting_with_empty_table_name() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[.b]"));
  }
  
  @Test
  public void should_fail_on_nested_table_array_name_ending_with_empty_table_name() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[[a.]]"));
  }
  
  @Test
  public void should_fail_on_nested_table_array_name_containing_empty_table_name() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[[a..b]]"));
  }
  
  @Test
  public void should_fail_on_nested_table_array_name_starting_with_empty_table_name() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("[[.b]]"));
  }
}

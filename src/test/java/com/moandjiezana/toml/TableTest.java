package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TableTest {
  
  @Test
  public void should_get_table() throws Exception {
    Toml toml = new Toml().read("[group]\nkey = \"value\"");

    Toml group = toml.getTable("group");

    assertEquals("value", group.getString("key"));
  }

  @Test
  public void should_get_value_for_multi_key() throws Exception {
    Toml toml = new Toml().read("[group]\nkey = \"value\"");

    assertEquals("value", toml.getString("group.key"));
  }

  @Test
  public void should_get_value_for_multi_key_with_no_parent_table() throws Exception {
    Toml toml = new Toml().read("[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getString("group.sub.key"));
  }

  @Test
  public void should_get_table_for_multi_key() throws Exception {
    Toml toml = new Toml().read("[group]\nother=1\n[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getTable("group.sub").getString("key"));
  }

  @Test
  public void should_get_table_for_multi_key_with_no_parent_table() throws Exception {
    Toml toml = new Toml().read("[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getTable("group.sub").getString("key"));
  }

  @Test
  public void should_get_value_from_table_with_sub_table() throws Exception {
    Toml toml = new Toml().read("[a.b]\nc=1\n[a]\nd=2");

    assertEquals(2, toml.getLong("a.d").intValue());
    assertEquals(1, toml.getTable("a.b").getLong("c").intValue());
  }
  
  @Test
  public void should_get_empty_table() throws Exception {
    Toml toml = new Toml().read("[a]");
    assertTrue(toml.getTable("a").isEmpty());
  }
  
  @Test
  public void should_return_null_for_missing_table() throws Exception {
    assertNull(new Toml().getTable("a"));
  }

  @Test
  public void should_accept_table_name_with_basic_string() {
    Toml toml = new Toml().read("[\"a\"]\nb = 'b'");

    assertEquals("b", toml.getString("\"a\".b"));
  }

  @Test
  public void should_accept_table_name_part_with_basic_string() {
    Toml toml = new Toml().read("[target.\"cfg(unix)\".dependencies]\nb = 'b'");

    assertEquals("b", toml.getString("target.\"cfg(unix)\".dependencies.b"));
  }

  @Test
  public void should_accept_table_name_with_literal_string() {
    Toml toml = new Toml().read("['a']\nb = 'b'");

    assertEquals("b", toml.getString("'a'.b"));
  }

  @Test
  public void should_accept_table_name_part_with_literal_string() {
    Toml toml = new Toml().read("[target.'cfg(unix)'.dependencies]\nb = 'b'");

    assertEquals("b", toml.getString("target.'cfg(unix)'.dependencies.b"));
  }
  
  @Test
  public void should_return_null_when_navigating_to_missing_value() throws Exception {
    Toml toml = new Toml();
    
    assertNull(toml.getString("a.b"));
    assertNull(toml.getList("a.b"));
    assertNull(toml.getTable("a.b"));
  }

  @Test
  public void should_return_null_when_no_value_for_multi_key() throws Exception {
    Toml toml = new Toml().read("");

    assertNull(toml.getString("group.key"));
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_table_defined_twice() throws Exception {
    new Toml().read("[a]\nb=1\n[a]\nc=2");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_illegal_characters_after_table() throws Exception {
    new Toml().read("[error]   if you didn't catch this, your parser is broken");
  }
}

package com.moandjiezana.toml;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TableTest {
  
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
  public void should_handle_navigation_to_missing_value() throws Exception {
    Toml toml = new Toml();
    
    assertNull(toml.getString("a.b"));
    assertNull(toml.getString("a.b[0].c"));
    assertThat(toml.getList("a.b"), hasSize(0));
    assertTrue(toml.getTable("a.b").isEmpty());
    assertTrue(toml.getTable("a.b[0]").isEmpty());
  }

  @Test
  public void should_return_null_when_no_value_for_multi_key() throws Exception {
    Toml toml = new Toml().parse("");

    assertNull(toml.getString("group.key"));
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_table_defined_twice() throws Exception {
    new Toml().parse("[a]\nb=1\n[a]\nc=2");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_illegal_characters_after_table() throws Exception {
    new Toml().parse("[error]   if you didn't catch this, your parser is broken");
  }
}

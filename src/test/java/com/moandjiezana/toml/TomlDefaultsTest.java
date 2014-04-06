package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;


public class TomlDefaultsTest {

  private Toml defaultToml;

  @Before
  public void before() {
    defaultToml = new Toml().parse("a = \"a\"\n[group]\na=\"a\"");
  }

  @Test
  public void should_fall_back_to_default_value() {
    Toml toml = new Toml(defaultToml);

    assertEquals("a", toml.getString("a"));
  }

  @Test
  public void should_use_value_when_present_in_values_and_defaults() {
    Toml toml = new Toml(defaultToml).parse("a = \"b\"");

    assertEquals("b", toml.getString("a"));
  }

  @Test
  public void should_return_null_when_no_defaults_for_key() throws Exception {
    Toml toml = new Toml(defaultToml).parse("");

    assertNull(toml.getString("b"));
  }

  @Test
  public void should_fall_back_to_default_with_multi_key() throws Exception {
    Toml toml = new Toml(defaultToml).parse("");

    assertEquals("a", toml.getString("group.a"));
  }

  @Test
  public void should_fall_back_to_table() throws Exception {
    Toml toml = new Toml(defaultToml).parse("");

    assertEquals("a", toml.getTable("group").getString("a"));
  }

  @Test
  public void should_fall_back_to_key_within_table() throws Exception {
    Toml toml = new Toml(defaultToml).parse("[group]\nb=1");

    assertEquals(1, toml.getTable("group").getLong("b").intValue());
    assertEquals("a", toml.getTable("group").getString("a"));
  }
}

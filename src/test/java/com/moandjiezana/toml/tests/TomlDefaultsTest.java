package com.moandjiezana.toml.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;


public class TomlDefaultsTest {

  private Toml defaultToml;

  @BeforeEach
  public void before() {
    defaultToml = new Toml().read("a = \"a\"\n [group]\n a=\"a\"\n [[array]]\n b=1\n [[array]]\n b=2");
  }

  @Test
  public void should_fall_back_to_default_value() {
    Toml toml = new Toml(defaultToml);

    assertEquals("a", toml.getString("a"));
  }

  @Test
  public void should_use_value_when_present_in_values_and_defaults() {
    Toml toml = new Toml(defaultToml).read("a = \"b\"");

    assertEquals("b", toml.getString("a"));
  }

  @Test
  public void should_return_null_when_no_defaults_for_key() throws Exception {
    Toml toml = new Toml(defaultToml).read("");

    assertNull(toml.getString("b"));
  }

  @Test
  public void should_fall_back_to_default_with_multi_key() throws Exception {
    Toml toml = new Toml(defaultToml).read("");

    assertEquals("a", toml.getString("group.a"));
  }

  @Test
  public void should_fall_back_to_table() throws Exception {
    Toml toml = new Toml(defaultToml).read("");

    assertEquals("a", toml.getTable("group").getString("a"));
  }

  @Test
  public void should_fall_back_to_table_array() throws Exception {
    Toml toml = new Toml(defaultToml).read("");

    assertThat(toml.getTables("array"), hasSize(2));
    assertThat(toml.getLong("array[1].b"), Matchers.equalTo(2L));
  }

  @Test
  public void should_perform_shallow_merge() throws Exception {
    Toml toml = new Toml(defaultToml).read("[group]\nb=1\n [[array]]\n b=0");
    Toml toml2 = new Toml(defaultToml).read("[[array]]\n b=1\n [[array]]\n b=2\n [[array]]\n b=3");

    assertEquals(1, toml.getTable("group").getLong("b").intValue());
    assertNull(toml.getTable("group").getString("a"));
    assertThat(toml.getTables("array"), hasSize(1));
    assertEquals(0, toml.getLong("array[0].b").intValue());
    assertThat(toml2.getTables("array"), hasSize(3));
  }
}

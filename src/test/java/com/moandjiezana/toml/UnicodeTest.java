package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnicodeTest {

  @Test
  public void should_support_short_escape_form() {
    Toml toml = new Toml().read("key = \"Jos\\u00E9\\nLocation\tSF\"");
    assertEquals("José\nLocation\tSF", toml.getString("key"));
  }

  @Test
  public void should_support_unicode_literal() {
    Toml toml = new Toml().read("key = \"José LöcÄtion SF\"");

    assertEquals("José LöcÄtion SF", toml.getString("key"));
  }

  @Test
  public void should_support_escaped_backslashes() {
    Toml toml = new Toml().read("key = \"C:\\\\Users\"");
    assertEquals("C:\\Users", toml.getString("key"));
  }
}

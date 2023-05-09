package com.moandjiezana.toml.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;

public class UnicodeTest {

  @Test
  public void should_support_short_escape_form() throws Exception {
    Toml toml = new Toml().read("key = \"Jos\u00E9\\nLocation\tSF\"");
    
    assertEquals("José\nLocation\tSF", toml.getString("key"));
  }

  @Test
  public void should_support_unicode_literal() throws Exception {
    Toml toml = new Toml().read("key = \"José LöcÄtion SF\"");
    
    assertEquals("José LöcÄtion SF", toml.getString("key"));
  }
}

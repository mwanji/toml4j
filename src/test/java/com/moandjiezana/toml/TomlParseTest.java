package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

public class TomlParseTest {

  @Test
  public void should_parse_input_stream() throws Exception {
    Toml toml = new Toml().parse(getClass().getResourceAsStream("should_load_from_file.toml"));

    assertEquals("value", toml.getString("key"));
  }

  @Test
  public void should_parse_reader() throws Exception {
    Toml toml = new Toml().parse(new StringReader("key=1"));

    assertEquals(1, toml.getLong("key").intValue());
  }
}

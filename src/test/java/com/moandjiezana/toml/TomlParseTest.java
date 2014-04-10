package com.moandjiezana.toml;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
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

  @Test
  public void should_fail_on_missing_file() throws Exception {
    try {
      new Toml().parse(new File("missing"));
      fail("Exception should have been thrown");
    } catch (RuntimeException e) {
      assertThat(e.getCause(), instanceOf(FileNotFoundException.class));
    }
  }

  @Test
  public void should_fail_on_io_error() throws Exception {
    Reader readerThatThrows = new Reader() {

      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException();
      }

      @Override
      public void close() throws IOException {}
    };

    try {
      new Toml().parse(readerThatThrows);
      fail("Exception should have been thrown");
    } catch (RuntimeException e) {
      assertThat(e.getCause(), instanceOf(IOException.class));
    }
  }
}

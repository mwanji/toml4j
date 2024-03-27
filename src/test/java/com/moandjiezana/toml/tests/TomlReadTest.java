package com.moandjiezana.toml.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;

public class TomlReadTest {

  @Test
  public void should_read_input_stream() throws Exception {
    Toml toml = new Toml().read(getClass().getResourceAsStream("should_load_from_file.toml"));

    assertEquals("value", toml.getString("key"));
  }

  @Test
  public void should_read_reader() throws Exception {
    Toml toml = new Toml().read(new StringReader("key=1"));

    assertEquals(1, toml.getLong("key").intValue());
  }

  @Test
  public void should_fail_on_missing_file() throws Exception {
    try {
      new Toml().read(new File("missing"));
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
      new Toml().read(readerThatThrows);
      fail("Exception should have been thrown");
    } catch (RuntimeException e) {
      assertThat(e.getCause(), instanceOf(IOException.class));
    }
  }

  @Test
  public void should_read_toml_without_defaults() {
    Toml toml1 = new Toml().read("a = 1");
    Toml toml2 = new Toml().read(toml1);

    assertEquals(toml1.getLong("a"), toml2.getLong("a"));
  }

  @Test
  public void should_read_toml_and_merge_with_defaults() {
    Toml toml1 = new Toml().read("a = 1\nc = 3\nd = 5");
    Toml toml2 = new Toml().read("b = 2\nc = 4");
    Toml mergedToml = new Toml(toml1).read(toml2);

    assertEquals(toml1.getLong("a"), mergedToml.getLong("a"));
    assertEquals(toml2.getLong("b"), mergedToml.getLong("b"));
    assertEquals(toml2.getLong("c"), mergedToml.getLong("c"));
    assertEquals(toml1.getLong("d"), mergedToml.getLong("d"));
  }
}

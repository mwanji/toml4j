package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.moandjiezana.toml.testutils.Utils;

public class ArrayTest {
  
  @Test
  public void should_get_array() throws Exception {
    Toml toml = new Toml().parse("list = [\"a\", \"b\", \"c\"]");

    assertEquals(asList("a", "b", "c"), toml.<String>getList("list"));
  }

  @Test
  public void should_allow_multiline_array() throws Exception {
    Toml toml = new Toml().parse(file("should_allow_multiline_array"));

    assertEquals(asList("a", "b", "c"), toml.<String>getList("a"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void should_get_nested_arrays() throws Exception {
    Toml clients = new Toml().parse("data = [ [\"gamma\", \"delta\"], [1, 2]] # just an update to make sure parsers support it");

    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.<String>getList("data"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void should_get_nested_arrays_with_no_space_between_outer_and_inner_array() throws Exception {
    Toml clients = new Toml().parse("data = [[\"gamma\", \"delta\"], [1, 2]] # just an update to make sure parsers support it");

    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.<String>getList("data"));
  }

  @Test
  public void should_return_empty_list_when_no_value_for_table_array() throws Exception {
    List<Toml> tomls = new Toml().parse("[a]").getTables("b");

    assertTrue(tomls.isEmpty());
  }

  @Test
  public void should_ignore_comma_at_end_of_array() throws Exception {
    Toml toml = new Toml().parse("key=[1,2,3,]");

    assertEquals(asList(1L, 2L, 3L), toml.<Long>getList("key"));
  }
  
  @Test
  public void should_support_mixed_string_types() throws Exception {
    Toml toml = new Toml().parse("key = [\"a\", 'b', \"\"\"c\"\"\", '''d''']");
    
    assertThat(toml.<String>getList("key"), contains("a", "b", "c", "d"));
  }
  
  @Test
  public void should_support_array_terminator_in_strings() throws Exception {
    Toml toml = new Toml().parse("key = [\"a]\", 'b]', \"\"\"c]\"\"\", '''d]''']");
    
    assertThat(toml.<String>getList("key"), contains("a]", "b]", "c]", "d]"));
  }
  
  private File file(String file) {
    return Utils.file(getClass(), file);
  }
}

package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.moandjiezana.toml.testutils.Utils;

public class ArrayTest {
  
  @Test
  public void should_get_array() throws Exception {
    Toml toml = new Toml().read("list = [\"a\", \"b\", \"c\"]");

    assertEquals(asList("a", "b", "c"), toml.<String>getList("list"));
  }

  @Test
  public void should_return_null_if_no_value_for_key() throws Exception {
    Toml toml = new Toml().read("");

    assertNull(toml.getList("a"));
  }

  @Test
  public void should_allow_multiline_array() throws Exception {
    Toml toml = new Toml().read(file("should_allow_multiline_array"));

    assertEquals(asList("a", "b", "c"), toml.<String>getList("a"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void should_get_nested_arrays() throws Exception {
    Toml clients = new Toml().read("data = [ [\"gamma\", \"delta\"], [1, 2]] # just an update to make sure parsers support it");

    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.<String>getList("data"));
  }
  
  @Test
  public void should_get_deeply_nested_arrays() throws Exception {
    List<List<?>> data = new Toml().read("data = [[[1], [2]], [3, 4]]").getList("data");
    
    assertThat(data, hasSize(2));
    assertEquals(Arrays.asList(1L), data.get(0).get(0));
    assertEquals(asList(2L), data.get(0).get(1));
    assertEquals(asList(3L, 4L), data.get(1));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void should_get_nested_arrays_with_no_space_between_outer_and_inner_array() throws Exception {
    Toml clients = new Toml().read("data = [[\"gamma\", \"delta\"], [1, 2]] # just an update to make sure parsers support it");

    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.<String>getList("data"));
  }

  @Test
  public void should_ignore_comma_at_end_of_array() throws Exception {
    Toml toml = new Toml().read("key=[1,2,3,]");

    assertEquals(asList(1L, 2L, 3L), toml.<Long>getList("key"));
  }
  
  @Test
  public void should_support_mixed_string_types() throws Exception {
    Toml toml = new Toml().read("key = [\"a\", 'b', \"\"\"c\"\"\", '''d''']");
    
    assertThat(toml.<String>getList("key"), contains("a", "b", "c", "d"));
  }
  
  @Test
  public void should_support_array_terminator_in_strings() throws Exception {
    Toml toml = new Toml().read("key = [\"a]\", 'b]', \"\"\"c]\"\"\", '''d]''']");
    
    assertThat(toml.<String>getList("key"), contains("a]", "b]", "c]", "d]"));
  }
  
  @Test
  public void should_support_array_of_inline_tables() throws Exception {
    Toml toml = new Toml().read(getClass().getResourceAsStream("should_support_array_of_inline_tables.toml"));
    
    assertThat(toml.getList("points"), hasSize(4));
    assertEquals(1, toml.getLong("points[0].x").longValue());
    assertEquals(2, toml.getLong("points[0].y").longValue());
    assertEquals(3, toml.getLong("points[0].z").longValue());
    assertEquals(7, toml.getLong("points[1].x").longValue());
    assertEquals(8, toml.getLong("points[1].y").longValue());
    assertEquals(9, toml.getLong("points[1].z").longValue());
    assertEquals(2, toml.getLong("points[2].x").longValue());
    assertEquals(4, toml.getLong("points[2].y").longValue());
    assertEquals(8, toml.getLong("points[2].z").longValue());
    assertEquals("3", toml.getString("points[3].x"));
    assertEquals("6", toml.getString("points[3].y"));
    assertEquals("12", toml.getString("points[3].z"));
  }
  
  private File file(String file) {
    return Utils.file(getClass(), file);
  }
}

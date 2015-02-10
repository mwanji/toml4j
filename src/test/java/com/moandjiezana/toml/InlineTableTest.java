package com.moandjiezana.toml;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

public class InlineTableTest {

  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  @Test
  public void should_read_empty_inline_table() throws Exception {
    Toml toml = new Toml().parse("key = {}");
    
    assertNotNull(toml.getTable("key"));
  }
  
  @Test
  public void should_read_inline_table_with_strings() throws Exception {
    Toml toml = new Toml().parse("name = { first = \"Tom\", last = \"Preston-Werner\"}");
    
    assertEquals("Tom", toml.getTable("name").getString("first"));
    assertEquals("Preston-Werner", toml.getString("name.last"));
  }
  
  @Test
  public void should_read_inline_table_with_integers() throws Exception {
    Toml toml = new Toml().parse("point = { x = 1, y = 2 }");
    
    assertEquals(1, toml.getTable("point").getLong("x").longValue());
    assertEquals(2, toml.getLong("point.y").longValue());
  }
  
  @Test
  public void should_read_inline_table_with_floats() throws Exception {
    Toml toml = new Toml().parse("point = { x = 1.5, y = 2.3 }");
    
    assertEquals(1.5, toml.getTable("point").getDouble("x").doubleValue(), 0);
    assertEquals(2.3, toml.getDouble("point.y").doubleValue(), 0);
  }
  
  @Test
  public void should_read_inline_table_with_booleans() throws Exception {
    Toml toml = new Toml().parse("point = { x = false, y = true }");
    
    assertTrue(toml.getTable("point").getBoolean("y"));
    assertFalse(toml.getBoolean("point.x"));
  }
  
  @Test
  public void should_read_inline_table_with_dates() throws Exception {
    Toml toml = new Toml().parse("point = { x = 2015-02-09T22:05:00Z, y = 2015-02-09T21:05:00Z }");

    Calendar x = Calendar.getInstance(UTC);
    x.set(2015, Calendar.FEBRUARY, 9, 22, 5, 00);
    x.set(Calendar.MILLISECOND, 0);
    
    Calendar y = Calendar.getInstance(UTC);
    y.set(2015, Calendar.FEBRUARY, 9, 21, 5, 00);
    y.set(Calendar.MILLISECOND, 0);

    assertEquals(x.getTime(), toml.getTable("point").getDate("x"));
    assertEquals(y.getTime(), toml.getDate("point.y"));
  }
  
  @Test
  public void should_read_arrays() throws Exception {
    Toml toml = new Toml().parse("arrays = { integers = [1, 2, 3], strings = [\"a\", \"b\", \"c\"] }");
    
    assertThat(toml.<Long>getList("arrays.integers"), contains(1L, 2L, 3L));
    assertThat(toml.<String>getList("arrays.strings"), contains("a", "b", "c"));
  }
  
  @Test
  public void should_read_nested_arrays() throws Exception {
    Toml toml = new Toml().parse("arrays = { nested = [[1, 2, 3], [4, 5, 6]] }").getTable("arrays");
    
    List<List<Long>> nested = toml.<List<Long>>getList("nested");
    assertThat(nested, hasSize(2));
    assertThat(nested.get(0), contains(1L, 2L, 3L));
    assertThat(nested.get(1), contains(4L, 5L, 6L));
  }
  
  @Test
  public void should_read_mixed_inline_table() throws Exception {
    Toml toml = new Toml().parse("point = { date = 2015-02-09T22:05:00Z, bool = true, integer = 123, float = 123.456, string = \"abc\", list = [5, 6, 7, 8] }").getTable("point");


    Calendar date = Calendar.getInstance(UTC);
    date.set(2015, Calendar.FEBRUARY, 9, 22, 5, 00);
    date.set(Calendar.MILLISECOND, 0);
    
    assertEquals(date.getTime(), toml.getDate("date"));
    assertTrue(toml.getBoolean("bool"));
    assertEquals(123, toml.getLong("integer").intValue());
    assertEquals(123.456, toml.getDouble("float"), 0);
    assertEquals("abc", toml.getString("string"));
    assertThat(toml.<Long>getList("list"), contains(5L, 6L, 7L, 8L));
  }
}

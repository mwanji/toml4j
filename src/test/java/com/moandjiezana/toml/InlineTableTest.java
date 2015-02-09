package com.moandjiezana.toml;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
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
  public void should_support_array_of_inline_tables() throws Exception {
    Toml toml = new Toml().parse(getClass().getResourceAsStream("should_support_array_of_inline_tables.toml"));
    
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
  }
}

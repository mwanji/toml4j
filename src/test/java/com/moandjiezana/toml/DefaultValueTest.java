package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class DefaultValueTest {
  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
  
  private static final Calendar CALENDAR = Calendar.getInstance(UTC);
  private static final Date _2011_11_10;
  private static final Date _2012_11_10;
  
  static {
    CALENDAR.set(2011, Calendar.NOVEMBER, 10, 13, 12, 00);
    CALENDAR.set(Calendar.MILLISECOND, 0);
    _2011_11_10 = CALENDAR.getTime();
    CALENDAR.set(Calendar.YEAR, 2012);
    _2012_11_10 = CALENDAR.getTime();
  }

  @Test
  public void should_get_string() throws Exception {
    Toml toml = new Toml().parse("s = \"string value\"");
    assertEquals("string value", toml.getString("s", "default string value"));
  }

  @Test
  public void should_get_string_default_value() throws Exception {
    Toml toml = new Toml().parse("");
    assertEquals("default string value", toml.getString("s", "default string value"));
  }

  @Test
  public void should_get_long() throws Exception {
    Toml toml = new Toml().parse("n = 1001");
    assertEquals(Long.valueOf(1001), toml.getLong("n", 1002L));
  }

  @Test
  public void should_get_long_default_value() throws Exception {
    Toml toml = new Toml().parse("");
    assertEquals(Long.valueOf(1002), toml.getLong("n", 1002L));
  }

  @Test
  public void should_get_double() throws Exception {
    Toml toml = new Toml().parse("n = 0.5");
    assertEquals(Double.valueOf(0.5), toml.getDouble("n", Double.valueOf(0.6)));
  }

  @Test
  public void should_get_double_default_value() throws Exception {
    Toml toml = new Toml().parse("");
    assertEquals(Double.valueOf(0.6), toml.getDouble("n", Double.valueOf(0.6)));
  }

  @Test
  public void should_get_boolean() throws Exception {
    Toml toml = new Toml().parse("b = true");
    assertEquals(Boolean.TRUE, toml.getBoolean("b", Boolean.FALSE));
  }

  @Test
  public void should_get_boolean_default_value() throws Exception {
    Toml toml = new Toml().parse("");
    assertEquals(Boolean.FALSE, toml.getBoolean("b", Boolean.FALSE));
  }

  @Test
  public void should_get_date() throws Exception {
    Toml toml = new Toml().parse("d = 2011-11-10T13:12:00Z");

    assertEquals(_2011_11_10, toml.getDate("d", _2012_11_10));
  }

  @Test
  public void should_get_date_default_value() throws Exception {
    Toml toml = new Toml().parse("");

    assertEquals(_2012_11_10, toml.getDate("d", _2012_11_10));
  }
  
  @Test
  public void should_get_array() throws Exception {
    Toml toml = new Toml().parse("a = [1, 2, 3]\n  b = []");
    
    assertEquals(asList(1L, 2L, 3L), toml.getList("a", asList(3L, 2L, 1L)));
    assertEquals(Collections.emptyList(), toml.getList("b", asList(3L, 2L, 1L)));
  }
  
  @Test
  public void should_get_empty_array() throws Exception {
    Toml toml = new Toml().parse("a = []");
    
    assertEquals(Collections.emptyList(), toml.getList("a", asList(3L, 2L, 1L)));
  }
  
  @Test
  public void should_get_array_default_value() throws Exception {
    Toml toml = new Toml();
    
    assertEquals(asList(3L, 2L, 1L), toml.getList("a", asList(3L, 2L, 1L)));
  }
  
  @Test
  public void should_prefer_default_from_constructor() throws Exception {
    Toml defaults = new Toml().parse("n = 1\n d = 1.1\n  b = true\n  date = 2011-11-10T13:12:00Z\n  s = 'a'\n  a = [1, 2, 3]");
    Toml toml = new Toml(defaults).parse("");
    
    assertEquals(1, toml.getLong("n", 2L).intValue());
    assertEquals(1.1, toml.getDouble("d", 2.2), 0);
    assertTrue(toml.getBoolean("b", false));
    assertEquals(_2011_11_10, toml.getDate("date", _2012_11_10));
    assertEquals("a", toml.getString("s", "b"));
    assertEquals(asList(1L, 2L, 3L), toml.getList("a", asList(3L, 2L, 1L)));
  }
}

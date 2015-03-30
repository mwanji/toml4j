package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class DefaultValueTest {
  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

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

    Calendar calendar = Calendar.getInstance(UTC);
    calendar.set(2011, Calendar.NOVEMBER, 10, 13, 12, 00);
    calendar.set(Calendar.MILLISECOND, 0);
    Date expected = calendar.getTime();
    calendar.set(2012, Calendar.NOVEMBER, 10, 13, 12, 00);
    Date defaultValue = calendar.getTime();
    assertEquals(expected, toml.getDate("d", defaultValue));
  }

  @Test
  public void should_get_date_default_value() throws Exception {
    Toml toml = new Toml().parse("");

    Calendar calendar = Calendar.getInstance(UTC);
    calendar.set(2012, Calendar.NOVEMBER, 10, 13, 12, 00);
    calendar.set(Calendar.MILLISECOND, 0);
    Date defaultValue = calendar.getTime();
    assertEquals(defaultValue, toml.getDate("d", defaultValue));
  }
  
  @Test
  public void should_prefer_default_from_constructor() throws Exception {
    Toml defaults = new Toml().parse("n = 1\n d = 1.1\n  b = true\n  date = 2011-11-10T13:12:00Z\n  s = 'a'\n  a = [1, 2, 3]");
    Toml toml = new Toml(defaults).parse("");
    
    assertEquals(1, toml.getLong("n", 2L).intValue());
    assertEquals(1.1, toml.getDouble("d", 2.2), 0);
    assertTrue(toml.getBoolean("b", false));
    Calendar calendar = Calendar.getInstance(UTC);
    calendar.set(2011, Calendar.NOVEMBER, 10, 13, 12, 00);
    calendar.set(Calendar.MILLISECOND, 0);
    Date expected = calendar.getTime();
    calendar.set(Calendar.YEAR, 2012);
    Date defaultValue = calendar.getTime();
    assertEquals(expected, toml.getDate("date", defaultValue));
    assertEquals("a", toml.getString("s", "b"));
  }
}

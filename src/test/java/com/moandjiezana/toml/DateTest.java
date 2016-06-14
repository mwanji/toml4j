package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateTest {
  
  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  @Test
  public void should_get_date() throws Exception {
    Toml toml = new Toml().read("a_date = 2011-11-10T13:12:00Z");

    Calendar calendar = Calendar.getInstance(UTC);
    calendar.set(2011, Calendar.NOVEMBER, 10, 13, 12, 00);
    calendar.set(Calendar.MILLISECOND, 0);

    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }

  @Test
  public void should_get_date_with_offset() throws Exception {
    Toml toml = new Toml().read("a_date = 1979-05-27T00:32:00-07:00");

    Calendar calendar = Calendar.getInstance(UTC);
    calendar.set(1979, Calendar.MAY, 27, 7, 32, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }

  @Test
  public void should_get_date_with_positive_offset() throws Exception {
    Toml toml = new Toml().read("a_date = 1979-05-27T07:32:00+07:00");

    Calendar calendar = Calendar.getInstance(UTC);
    calendar.set(1979, Calendar.MAY, 27, 0, 32, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }
  
  @Test
  public void should_get_date_with_fractional_seconds() throws Exception {
    Toml toml = new Toml().read("a_date = 1979-05-27T00:32:00.999Z");
    
    Calendar calendar = Calendar.getInstance(UTC);
    calendar.set(1979, Calendar.MAY, 27, 0, 32, 00);
    calendar.set(Calendar.MILLISECOND, 999);
    
    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }
  
  @Test
  public void should_get_date_with_fractional_seconds_and_offset() throws Exception {
    Toml toml = new Toml().read("a_date = 1979-05-27T00:32:00.999-07:00");
    
    Calendar calendar = Calendar.getInstance(UTC);
    calendar.set(1979, Calendar.MAY, 27, 7, 32, 00);
    calendar.set(Calendar.MILLISECOND, 999);
    
    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_non_existant_date() throws Exception {
    new Toml().read("d = 2012-13-01T15:00:00Z");
  }
}

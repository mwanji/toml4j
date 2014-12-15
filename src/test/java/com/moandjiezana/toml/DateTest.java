package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateTest {
  
  @Test
  public void should_get_date() throws Exception {
    Toml toml = new Toml().parse("a_date = 2011-11-10T13:12:00Z");

    Calendar calendar = Calendar.getInstance();
    calendar.set(2011, Calendar.NOVEMBER, 10, 13, 12, 00);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }

  public static void main(String[] args) {
    for (String tz : TimeZone.getAvailableIDs(-7 * 60 * 60 * 1000)) {
      System.out.println(tz);
    }
  }
  
  @Test
  public void should_get_date_with_timezone_offset() throws Exception {
    Toml toml = new Toml().parse("a_date = 1979-05-27T00:32:00-07:00");

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT-7"));
    calendar.set(1979, Calendar.MAY, 27, 0, 32, 00);
    calendar.set(Calendar.MILLISECOND, 0);

    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }
  
  @Test
  public void should_get_date_with_fractional_seconds() throws Exception {
    Toml toml = new Toml().parse("a_date = 1979-05-27T00:32:00.999-07:00");
    
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT-7"));
    calendar.set(1979, Calendar.MAY, 27, 0, 32, 00);
    calendar.set(Calendar.MILLISECOND, 999);
    
    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_non_existant_date() throws Exception {
    new Toml().parse("d = 2012-13-01T15:00:00Z");
  }
}

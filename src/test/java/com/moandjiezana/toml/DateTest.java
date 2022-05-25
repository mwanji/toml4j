package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class DateTest {
  
  @Test
  public void should_get_date_with_offset() throws Exception {
    Toml toml = new Toml().read("a_offset_datetime = 2011-11-10T13:12:00Z");
    final OffsetDateTime offsetDateTime = OffsetDateTime.of(2011, 11, 10, 13, 12, 00, 00, ZoneOffset.UTC);

    assertEquals(offsetDateTime, toml.getOffsetDateTime("a_offset_datetime"));
  }

  @Test
  public void should_get_date_with_fractional_seconds_and_offset() throws Exception {
    Toml toml = new Toml().read("a_offset_datetime = 1979-05-27T00:32:00.999-07:00");
    final OffsetDateTime offsetDateTime = OffsetDateTime.of(1979, 05, 27, 00, 32, 00, 999000000, ZoneOffset.ofHours(-7));

    assertEquals(offsetDateTime, toml.getOffsetDateTime("a_offset_datetime"));
  }

  @Test
  public void should_get_local_date_time() throws Exception {
    Toml toml = new Toml().read("a_local_datetime = 1979-05-27T07:32:00");
    final LocalDateTime localDateTime = LocalDateTime.of(1979, 05, 27, 07, 32, 00);

    assertEquals(localDateTime, toml.getLocalDateTime("a_local_datetime"));
  }

  @Test
  public void should_get_local_date_time_with_fractional_seconds() throws Exception {
    Toml toml = new Toml().read("a_local_datetime = 1979-05-27T00:32:00.999999");
    final LocalDateTime localDateTime = LocalDateTime.of(1979, 05, 27, 00, 32, 00, 999999000);

    assertEquals(localDateTime, toml.getLocalDateTime("a_local_datetime"));
  }

  @Test
  public void should_get_local_date() throws Exception {
    Toml toml = new Toml().read("a_local_date = 1979-05-27");
    final LocalDate localDate = LocalDate.of(1979, 05, 27);

    assertEquals(localDate, toml.getLocalDate("a_local_date"));
  }

  @Test
  public void should_get_local_time() throws Exception {
    Toml toml = new Toml().read("a_local_time = 07:32:00");
    final LocalTime localTime = LocalTime.of(07, 32, 00);

    assertEquals(localTime, toml.getLocalTime("a_local_time"));
  }

  @Test
  public void should_get_local_time_with_fractional_seconds() throws Exception {
    Toml toml = new Toml().read("a_local_time = 00:32:00.999999");
    final LocalTime localTime = LocalTime.of(00, 32, 00, 999999000);

    assertEquals(localTime, toml.getLocalTime("a_local_time"));
  }
}

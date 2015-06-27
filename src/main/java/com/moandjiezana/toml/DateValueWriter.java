package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class DateValueWriter implements ValueWriter {
  static final ValueWriter DATE_VALUE_WRITER = new DateValueWriter();
  private static final Calendar calendar = new GregorianCalendar();

  @Override
  public boolean canWrite(Object value) {
    return value instanceof Date;
  }

  @Override
  public void write(Object value, WriterContext context) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss");
    context.output.append(dateFormat.format(value));
    int tzOffset = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
    context.output.append(String.format("%+03d:%02d", tzOffset / 60, tzOffset % 60));
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  @Override
  public boolean isTable() {
    return false;
  }
}

package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class DateSerializer implements Serializer {
  static final Serializer DATE_SERIALIZER = new DateSerializer();
  private static final Calendar calendar = new GregorianCalendar();

  @Override
  public boolean canSerialize(Object value) {
    return value instanceof Date;
  }

  @Override
  public void serialize(Object value, SerializerContext context) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss");
    context.serialized.append(dateFormat.format(value));
    int tzOffset = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
    context.serialized.append(String.format("%+03d:%02d", tzOffset / 60, tzOffset % 60));
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

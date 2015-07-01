package com.moandjiezana.toml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DateConverter implements ValueConverter, ValueWriter {

  static final DateConverter DATE_PARSER = new DateConverter();
  static final DateConverter DATE_PARSER_JDK_6 = new DateConverterJdk6();
  private static final Pattern DATE_REGEX = Pattern.compile("(\\d{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9])(\\.\\d*)?(Z|(?:[+\\-]\\d{2}:\\d{2}))(.*)");

  @Override
  public boolean canConvert(String s) {
    if (s.length() < 5) {
      return false;
    }
    
    for (int i = 0; i < 5; i++) {
      char c = s.charAt(i);
      
      if (i < 4) {
        if (!Character.isDigit(c)) {
          return false;
        }
      } else if (c != '-') {
        return false;
      }
    }

    return true;
  }

  @Override
  public Object convert(String original, AtomicInteger index, Context context) {
    StringBuilder sb = new StringBuilder();
    
    for (int i = index.get(); i < original.length(); i = index.incrementAndGet()) {
      char c = original.charAt(i);
      if (Character.isDigit(c) || c == '-' || c == ':' || c == '.' || c == 'T' || c == 'Z') {
        sb.append(c);
      } else {
        index.decrementAndGet();
        break;
      }
    }
    
    String s = sb.toString();
    Matcher matcher = DATE_REGEX.matcher(s);
    
    if (!matcher.matches()) {
      Results.Errors errors = new Results.Errors();
      errors.invalidValue(context.identifier.getName(), s, context.line.get());
      return errors;
    }
    
    String dateString = matcher.group(1);
    String zone = matcher.group(3);
    String fractionalSeconds = matcher.group(2);
    String format = "yyyy-MM-dd'T'HH:mm:ss";
    if (fractionalSeconds != null && !fractionalSeconds.isEmpty()) {
      format += ".SSS";
      dateString += fractionalSeconds;
    }
    format += "Z";
    if ("Z".equals(zone)) {
      dateString += "+0000";
    } else if (zone.contains(":")) {
      dateString += zone.replace(":", "");
    }

    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);
      dateFormat.setLenient(false);
      return dateFormat.parse(dateString);
    } catch (Exception e) {
      Results.Errors errors = new Results.Errors();
      errors.invalidValue(context.identifier.getName(), s, context.line.get());
      return errors;
    }
  }

  @Override
  public boolean canWrite(Object value) {
    return value instanceof Date;
  }

  @Override
  public void write(Object value, WriterContext context) {
    DateFormat formatter = getFormatter(context.getTimeZone());
    context.write(formatter.format(value));
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }
  
  private DateFormat getFormatter(TimeZone timeZone) {
    String format = "UTC".equals(timeZone.getID()) ? "yyyy-MM-dd'T'HH:m:ss'Z'" : "yyyy-MM-dd'T'HH:m:ssXXX";
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    formatter.setTimeZone(timeZone);
    
    return formatter;
  }
  
  private DateConverter() {}
  
  private static class DateConverterJdk6 extends DateConverter {
    @Override
    public void write(Object value, WriterContext context) {
      TimeZone timeZone = context.getTimeZone();
      DateFormat formatter = getFormatter(timeZone);
      String date = formatter.format(value);
      
      if ("UTC".equals(timeZone.getID())) {
        context.write(date);
      } else {
        context.write(date.substring(0, 22)).write(':').write(date.substring(22));
      }
    }

    private DateFormat getFormatter(TimeZone timeZone) {
      String format = "UTC".equals(timeZone.getID()) ? "yyyy-MM-dd'T'HH:m:ss'Z'" : "yyyy-MM-dd'T'HH:m:ssZ";
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      formatter.setTimeZone(timeZone);
      return formatter;
    }
  }

  @Override
  public String toString() {
    return "datetime";
  }
}

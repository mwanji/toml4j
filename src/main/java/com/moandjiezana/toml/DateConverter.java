package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DateConverter implements ValueConverter {

  static final DateConverter DATE_PARSER = new DateConverter();
  private static final Pattern DATE_REGEX = Pattern.compile("(\\d{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9])(\\.\\d*)?(Z|(?:[+\\-]\\d{2}:\\d{2}))(.*)");

  @Override
  public boolean canConvert(String s) {
    if (s.length() < 5) {
      return false;
    }
    
    char[] chars = s.toCharArray();

    for (int i = 0; i < 5; i++) {
      char c = chars[i];
      
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
  
  private DateConverter() {}
}

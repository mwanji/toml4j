package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DateConverter implements ValueConverter {

  static final DateConverter DATE_PARSER = new DateConverter();
  private static final Pattern DATE_REGEX = Pattern.compile("(\\d{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9])(\\.\\d*)?(Z|(?:[+\\-]\\d{2}:\\d{2}))(.*)");

  @Override
  public boolean canConvert(String s) {
    Matcher matcher = DATE_REGEX.matcher(s);

    return matcher.matches() && ValueConverterUtils.isComment(matcher.group(4));
  }

  @Override
  public Object convert(String s) {
    Matcher matcher = DATE_REGEX.matcher(s);
    matcher.matches();
    s = matcher.group(1);
    String zone = matcher.group(3);
    String fractionalSeconds = matcher.group(2);
    String format = "yyyy-MM-dd'T'HH:mm:ss";
    if (fractionalSeconds != null && !fractionalSeconds.isEmpty()) {
      format += ".SSS";
      s += fractionalSeconds;
    }
    format += "Z";
    if ("Z".equals(zone)) {
      s += "+0000";
    } else if (zone.contains(":")) {
      s += zone.replace(":", "");
    }
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);
      dateFormat.setLenient(false);
      return dateFormat.parse(s);
    } catch (Exception e) {
      return INVALID;
    }
  }
  
  private DateConverter() {}
}

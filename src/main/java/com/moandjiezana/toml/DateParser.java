package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueParserUtils.INVALID;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DateParser implements ValueParser {

  static final DateParser DATE_PARSER = new DateParser();
  private static final Pattern DATE_REGEX = Pattern.compile("(\\d{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z)(.*)");

  @Override
  public boolean canParse(String s) {
    Matcher matcher = DATE_REGEX.matcher(s);

    return matcher.matches() && ValueParserUtils.isComment(matcher.group(2));
  }

  @Override
  public Object parse(String s) {
    Matcher matcher = DATE_REGEX.matcher(s);
    matcher.matches();
    s = matcher.group(1).replace("Z", "+00:00");
    try {
      s = s.substring(0, 22) + s.substring(23);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
      dateFormat.setLenient(false);
      return dateFormat.parse(s);
    } catch (Exception e) {
      return INVALID;
    }
  }
  
  private DateParser() {}
}

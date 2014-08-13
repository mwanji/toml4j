package com.moandjiezana.toml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FloatConverter implements ValueConverter {

  public static final FloatConverter FLOAT_PARSER = new FloatConverter();
  private static final Pattern FLOAT_REGEX = Pattern.compile("(-?\\d+\\.\\d+)(.*)");
  
  @Override
  public boolean canConvert(String s) {
    Matcher matcher = FLOAT_REGEX.matcher(s);
    
    return matcher.matches() && ValueConverterUtils.isComment(matcher.group(2));
  }

  @Override
  public Object convert(String s) {
    Matcher matcher = FLOAT_REGEX.matcher(s);
    matcher.matches();
    
    return Double.valueOf(matcher.group(1));
  }
  
  private FloatConverter() {}

}

package com.moandjiezana.toml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FloatParser implements ValueParser {

  public static final FloatParser FLOAT_PARSER = new FloatParser();
  private static final Pattern FLOAT_REGEX = Pattern.compile("(-?\\d+\\.\\d+)(.*)");
  
  @Override
  public boolean canParse(String s) {
    Matcher matcher = FLOAT_REGEX.matcher(s);
    
    return matcher.matches() && ValueParserUtils.isComment(matcher.group(2));
  }

  @Override
  public Object parse(String s) {
    Matcher matcher = FLOAT_REGEX.matcher(s);
    matcher.matches();
    
    return Double.valueOf(matcher.group(1));
  }
  
  private FloatParser() {}

}

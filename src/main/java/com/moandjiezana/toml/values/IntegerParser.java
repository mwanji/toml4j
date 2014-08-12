package com.moandjiezana.toml.values;

import static com.moandjiezana.toml.values.ValueParserUtils.isComment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegerParser implements ValueParser {

  private static final Pattern INTEGER_REGEX = Pattern.compile("(-?[0-9]*)(.*)");
  static final IntegerParser INTEGER_PARSER = new IntegerParser();

  @Override
  public boolean canParse(String s) {
    Matcher matcher = INTEGER_REGEX.matcher(s);

    return matcher.matches() && isComment(matcher.group(2));
  }

  @Override
  public Object parse(String s) {
    Matcher matcher = INTEGER_REGEX.matcher(s);
    matcher.matches();
    
    return Long.valueOf(matcher.group(1));
  }

}

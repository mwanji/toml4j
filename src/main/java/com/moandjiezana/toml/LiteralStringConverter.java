package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;


class LiteralStringConverter implements ValueConverter {

  static final LiteralStringConverter LITERAL_STRING_PARSER = new LiteralStringConverter();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'");
  }

  @Override
  public Object convert(String s) {
    char[] chars = s.toCharArray();
    int endIndex = -1;
    
    for (int i = 1; i < chars.length; i++) {
      char c = chars[i];
      
      if (c == '\'') {
        endIndex = i;
        continue;
      }
      
      if (endIndex > -1 && c == '#') {
        break;
      }
      
      if (endIndex > -1 && !Character.isWhitespace(c)) {
        return INVALID;
      }
    }
    
    if (endIndex == -1) {
      return INVALID;
    }
    
    return s.substring(1, endIndex);
  }

  private LiteralStringConverter() {}
}

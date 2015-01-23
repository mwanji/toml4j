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
    boolean terminated = false;
    StringBuilder sb = new StringBuilder(s.length());
    
    for (int i = 1; i < chars.length; i++) {
      char c = chars[i];
      
      if (c == '\'') {
        terminated = true;
        continue;
      }
      
      if (!terminated) {
        sb.append(c);
      }
      
      if (terminated && c == '#') {
        break;
      }
      
      if (terminated && !Character.isWhitespace(c)) {
        return INVALID;
      }
    }
    
    if (!terminated) {
      return INVALID;
    }
    
    return sb.toString();
  }

  private LiteralStringConverter() {}
}

package com.moandjiezana.toml;


class IntegerConverter implements ValueConverter {
  static final IntegerConverter INTEGER_PARSER = new IntegerConverter();

  @Override
  public boolean canConvert(String s) {
    char[] chars = s.toCharArray();
    boolean whitespace = false;
    
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      
      if (Character.isDigit(c)) {
        continue;
      }
      
      if (i == 0 && (c == '+' || c == '-')) {
        continue;
      }
      
      if (Character.isWhitespace(c)) {
        whitespace = true;
        continue;
      }
      
      if (whitespace && c == '#') {
        return true;
      }
      
      return false;
    }
    
    return true;
  }

  @Override
  public Object convert(String s) {
    if (s.startsWith("+")) {
      s = s.substring(1);
    }
    
    int startOfComment = s.indexOf('#');
    if (startOfComment > -1) {
      s = s.substring(0, startOfComment).trim();
    }
    
    return Long.valueOf(s);
  }

  private IntegerConverter() {}
}

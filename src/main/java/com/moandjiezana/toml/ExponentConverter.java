package com.moandjiezana.toml;


class ExponentConverter implements ValueConverter {
  
  public static final ExponentConverter EXPONENT_PARSER = new ExponentConverter();

  @Override
  public boolean canConvert(String s) {
    char[] chars = s.toCharArray();
    boolean whitespace = false;
    boolean exponent = false;
    boolean signable = true;
    boolean decimal = false;
    
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      
      if (Character.isDigit(c)) {
        signable = false;
        continue;
      }
      
      if (signable && (c == '+' || c == '-') && chars.length > i + 1 && Character.isDigit(chars[i + 1])) {
        signable = false;
        continue;
      }
      
      if (i > 0 && (c == 'E' || c == 'e')) {
        signable = true;
        exponent = true;
        continue;
      }
      
      if (i > 0 && c == '.' && !decimal && !exponent) {
        decimal = true;
        continue;
      }
      
      if (Character.isWhitespace(c)) {
        whitespace = true;
        continue;
      }
      
      if (whitespace && c == '#') {
        break;
      }
      
      return false;
    }
    
    return exponent && !signable;
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
    
    String[] exponentString = s.split("[eE]");
    
    return Double.parseDouble(exponentString[0]) * Math.pow(10, Double.parseDouble(exponentString[1]));
  }
}

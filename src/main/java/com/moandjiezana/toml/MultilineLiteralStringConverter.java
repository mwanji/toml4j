package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

class MultilineLiteralStringConverter implements ValueConverter {
  
  static final MultilineLiteralStringConverter MULTILINE_LITERAL_STRING_CONVERTER = new MultilineLiteralStringConverter(); 
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'''");
  }

  @Override
  public Object convert(String s) {
    char[] chars = s.toCharArray();
    boolean terminated = false;
    StringBuilder sb = new StringBuilder(s.length());
    
    for (int i = 3; i < chars.length; i++) {
      char c = chars[i];
      
      if (c == '\'' && chars.length > i + 2 && chars[i + 1] == '\'' && chars[i + 2] == '\'') {
        i += 2;
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

    return sb.toString();
  }

  private MultilineLiteralStringConverter() {}
}

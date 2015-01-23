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
    int endIndex = -1;
    
    for (int i = 3; i < chars.length; i++) {
      char c = chars[i];
      
      if (c == '\'' && chars.length > i + 2 && chars[i + 1] == '\'' && chars[i + 2] == '\'') {
        endIndex = i;
        i += 2;
        continue;
      }
      
      if (endIndex > -1 && c == '#') {
        break;
      }
      
      if (endIndex > -1 && !Character.isWhitespace(c)) {
        return INVALID;
      }
    }

    return s.substring(3, endIndex);
  }

  private MultilineLiteralStringConverter() {}
}

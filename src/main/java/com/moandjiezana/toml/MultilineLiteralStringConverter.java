package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import static com.moandjiezana.toml.ValueConverterUtils.isComment;

import java.util.concurrent.atomic.AtomicInteger;

class MultilineLiteralStringConverter implements ValueConverter {
  
  static final MultilineLiteralStringConverter MULTILINE_LITERAL_STRING_CONVERTER = new MultilineLiteralStringConverter(); 
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'''");
  }

  @Override
  public Object convert(String s) {
    AtomicInteger index = new AtomicInteger();
    Object converted = convert(s, index);
    
    if (converted == INVALID || !isComment(s.substring(index.incrementAndGet()))) {
      return INVALID;
    }
    
    return converted;
  }

  @Override
  public Object convert(String s, AtomicInteger index) {
    char[] chars = s.toCharArray();
    int startIndex = index.addAndGet(3);
    int endIndex = -1;
    
    for (int i = startIndex; i < chars.length; i = index.incrementAndGet()) {
      char c = chars[i];
      
      if (c == '\'' && chars.length > i + 2 && chars[i + 1] == '\'' && chars[i + 2] == '\'') {
        endIndex = i;
        index.addAndGet(2);
        break;
      }
    }

    return s.substring(startIndex, endIndex);
  }

  private MultilineLiteralStringConverter() {}
}

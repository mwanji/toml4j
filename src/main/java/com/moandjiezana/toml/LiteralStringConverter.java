package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import static com.moandjiezana.toml.ValueConverterUtils.isComment;

import java.util.concurrent.atomic.AtomicInteger;


class LiteralStringConverter implements ValueConverter {

  static final LiteralStringConverter LITERAL_STRING_PARSER = new LiteralStringConverter();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'");
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
    boolean terminated = false;
    int startIndex = index.incrementAndGet();
    
    for (int i = index.get(); i < chars.length; i = index.incrementAndGet()) {
      char c = chars[i];
      
      if (c == '\'') {
        terminated = true;
        break;
      }
    }
    
    if (!terminated) {
      return INVALID;
    }
    
    String substring = s.substring(startIndex, index.get());
    
    return substring;
  }

  private LiteralStringConverter() {}
}

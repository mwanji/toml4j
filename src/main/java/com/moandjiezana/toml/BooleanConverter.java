package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import static com.moandjiezana.toml.ValueConverterUtils.isComment;

import java.util.concurrent.atomic.AtomicInteger;


class BooleanConverter implements ValueConverter {
  
  static final BooleanConverter BOOLEAN_PARSER = new BooleanConverter(); 

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("true") || s.startsWith("false");
  }

  @Override
  public Object convert(String s) {
    AtomicInteger index = new AtomicInteger();
    Object converted = convert(s, index);
    
    if (!isComment(s.substring(index.incrementAndGet()))) {
      return INVALID;
    }

    return converted;
  }

  @Override
  public Object convert(String s, AtomicInteger index) {
    s = s.substring(index.get());
    Boolean b = s.startsWith("true") ? Boolean.TRUE : Boolean.FALSE;
    
    int endIndex = b == Boolean.TRUE ? 4 : 5;
    
    index.addAndGet(endIndex - 1);
    
    return b;
  }

  private BooleanConverter() {}
}

package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;


class BooleanConverter implements ValueConverter {
  
  static final BooleanConverter BOOLEAN_PARSER = new BooleanConverter(); 

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("true") || s.startsWith("false");
  }

  @Override
  public Object convert(String s) {
    Boolean b = s.startsWith("true") ? Boolean.TRUE : Boolean.FALSE;
    
    int endIndex = b == Boolean.TRUE ? 4 : 5;
    
    if (!ValueConverterUtils.isComment(s.substring(endIndex))) {
      return INVALID;
    }
    
    return b;
  }

  private BooleanConverter() {}
}

package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import static com.moandjiezana.toml.ValueConverterUtils.parse;
import static com.moandjiezana.toml.ValueConverterUtils.parser;

import java.util.List;

class BooleanConverter implements ValueConverter {
  
  static final BooleanConverter BOOLEAN_PARSER = new BooleanConverter(); 

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("true") || s.startsWith("false");
  }

  @Override
  public Object convert(String s) {
    List<String> resultValue = parse(parser().Boolean(), s);
    
    if (resultValue == null) {
      return INVALID;
    }
    
    return Boolean.valueOf(resultValue.get(0));
  }

  private BooleanConverter() {}
}

package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.parse;
import static com.moandjiezana.toml.ValueConverterUtils.parser;

class ExponentConverter implements ValueConverter {
  
  public static final ExponentConverter EXPONENT_PARSER = new ExponentConverter();

  @Override
  public boolean canConvert(String s) {
    return parse(parser().Exponent(), s) != null;
  }

  @Override
  public Object convert(String s) {
    String[] exponentString = ((String) parse(parser().Exponent(), s).get(0)).split("[eE]");
    
    return Math.pow(Double.parseDouble(exponentString[0]), Double.parseDouble(exponentString[1]));
  }
}

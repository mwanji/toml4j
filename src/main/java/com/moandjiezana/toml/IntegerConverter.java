package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.parse;
import static com.moandjiezana.toml.ValueConverterUtils.parser;

import java.util.List;

class IntegerConverter implements ValueConverter {
  static final IntegerConverter INTEGER_PARSER = new IntegerConverter();

  @Override
  public boolean canConvert(String s) {
    return parse(parser().Integer(), s) != null;
  }

  @Override
  public Object convert(String s) {
    List<String> resultValue = parse(parser().Integer(), s);
    
    if (resultValue == null) {
      return ValueConverterUtils.INVALID;
    }

    return Long.valueOf(resultValue.get(0));
  }

  private IntegerConverter() {}
}

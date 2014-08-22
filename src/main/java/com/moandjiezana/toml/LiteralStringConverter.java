package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.parse;
import static com.moandjiezana.toml.ValueConverterUtils.parser;

import java.util.List;

class LiteralStringConverter implements ValueConverter {

  static final LiteralStringConverter LITERAL_STRING_PARSER = new LiteralStringConverter();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'");
  }

  @Override
  public Object convert(String s) {
    List<String> resultValue = parse(parser().LiteralString(), s);
    
    if (resultValue == null) {
      return ValueConverterUtils.INVALID;
    }
    
    return resultValue.get(0);
  }

  private LiteralStringConverter() {}
}

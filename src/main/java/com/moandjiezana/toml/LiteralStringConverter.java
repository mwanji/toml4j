package com.moandjiezana.toml;

import java.util.List;

import org.parboiled.Parboiled;

class LiteralStringConverter implements ValueConverter {

  static final LiteralStringConverter LITERAL_STRING_PARSER = new LiteralStringConverter();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'");
  }

  @Override
  public Object convert(String s) {
    ValueParser parser = Parboiled.createParser(ValueParser.class);
    List<String> resultValue = ValueConverterUtils.parse(ValueConverterUtils.parser().LiteralString(), s);
    
    if (resultValue == null) {
      return ValueConverterUtils.INVALID;
    }
    
    return resultValue.get(0);
  }

  private LiteralStringConverter() {}
}

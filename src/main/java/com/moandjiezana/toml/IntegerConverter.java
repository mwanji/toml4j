package com.moandjiezana.toml;

import java.util.List;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;

class IntegerConverter implements ValueConverter {
  static final IntegerConverter INTEGER_PARSER = new IntegerConverter();

  @Override
  public boolean canConvert(String s) {
    ValueParser parser = Parboiled.createParser(ValueParser.class);
    return new BasicParseRunner<Object>(parser.Integer()).run(s).resultValue != null;
  }

  @Override
  public Object convert(String s) {
    ValueParser parser = Parboiled.createParser(ValueParser.class);
    List<String> resultValue = new BasicParseRunner<List<String>>(parser.Integer()).run(s).resultValue;
    
    if (resultValue == null) {
      return ValueConverterUtils.INVALID;
    }

    return Long.valueOf(resultValue.get(0));
  }

  private IntegerConverter() {}
}

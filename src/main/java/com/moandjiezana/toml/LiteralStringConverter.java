package com.moandjiezana.toml;

import java.util.List;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;

class LiteralStringConverter implements ValueConverter {

  static final LiteralStringConverter LITERAL_STRING_PARSER = new LiteralStringConverter();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'");
  }

  @Override
  public Object convert(String s) {
    StatementParser parser = Parboiled.createParser(StatementParser.class);
    List<Object> resultValue = new BasicParseRunner<List<Object>>(parser.LiteralString()).run(s).resultValue;
    
    if (resultValue == null) {
      return ValueConverterUtils.INVALID;
    }
    
    return resultValue.get(0);
  }

  private LiteralStringConverter() {}
}

package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

import java.util.List;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;

class BooleanConverter implements ValueConverter {
  
  static final BooleanConverter BOOLEAN_PARSER = new BooleanConverter(); 

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("true") || s.startsWith("false");
  }

  @Override
  public Object convert(String s) {
    StatementParser parser = Parboiled.createParser(StatementParser.class);
    
    List<String> resultValue = new BasicParseRunner<List<String>>(parser.Boolean()).run(s).resultValue;
    
    if (resultValue == null) {
      return INVALID;
    }
    
    return Boolean.valueOf(resultValue.get(0));
  }

  private BooleanConverter() {}
}

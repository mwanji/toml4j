package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueParserUtils.INVALID;

import java.util.List;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;

class BooleanParser implements ValueParser {
  
  static final BooleanParser BOOLEAN_PARSER = new BooleanParser(); 

  @Override
  public boolean canParse(String s) {
    return s.startsWith("true") || s.startsWith("false");
  }

  @Override
  public Object parse(String s) {
    StatementParser parser = Parboiled.createParser(StatementParser.class);
    
    List<String> resultValue = new BasicParseRunner<List<String>>(parser.Boolean()).run(s).resultValue;
    
    if (resultValue == null) {
      return INVALID;
    }
    
    return Boolean.valueOf(resultValue.get(0));
  }

  private BooleanParser() {}
}

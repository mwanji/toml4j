package com.moandjiezana.toml;

import java.util.List;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;

class LiteralStringParser implements ValueParser {

  static final LiteralStringParser LITERAL_STRING_PARSER = new LiteralStringParser();
  
  @Override
  public boolean canParse(String s) {
    return s.startsWith("'");
  }

  @Override
  public Object parse(String s) {
    StatementParser parser = Parboiled.createParser(StatementParser.class);
    List<Object> resultValue = new BasicParseRunner<List<Object>>(parser.LiteralString()).run(s).resultValue;
    
    if (resultValue == null) {
      return ValueParserUtils.INVALID;
    }
    
    return resultValue.get(0);
  }

  private LiteralStringParser() {}
}

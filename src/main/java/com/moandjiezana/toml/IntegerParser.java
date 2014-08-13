package com.moandjiezana.toml;

import java.util.List;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;

class IntegerParser implements ValueParser {
  static final IntegerParser INTEGER_PARSER = new IntegerParser();

  @Override
  public boolean canParse(String s) {
    StatementParser parser = Parboiled.createParser(StatementParser.class);
    return new BasicParseRunner<Object>(parser.Integer()).run(s).resultValue != null;
  }

  @Override
  public Object parse(String s) {
    StatementParser parser = Parboiled.createParser(StatementParser.class);
    List<String> resultValue = new BasicParseRunner<List<String>>(parser.Integer()).run(s).resultValue;
    
    if (resultValue == null) {
      return ValueParserUtils.INVALID;
    }

    return Long.valueOf(resultValue.get(0));
  }

  private IntegerParser() {}
}

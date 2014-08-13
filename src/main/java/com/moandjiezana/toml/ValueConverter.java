package com.moandjiezana.toml;

import static com.moandjiezana.toml.ArrayParser.ARRAY_PARSER;
import static com.moandjiezana.toml.BooleanParser.BOOLEAN_PARSER;
import static com.moandjiezana.toml.DateParser.DATE_PARSER;
import static com.moandjiezana.toml.FloatParser.FLOAT_PARSER;
import static com.moandjiezana.toml.IntegerParser.INTEGER_PARSER;
import static com.moandjiezana.toml.LiteralStringParser.LITERAL_STRING_PARSER;
import static com.moandjiezana.toml.MultilineStringParser.MULTILINE_STRING_PARSER;
import static com.moandjiezana.toml.StringParser.STRING_PARSER;
import static com.moandjiezana.toml.ValueParserUtils.INVALID;

class ValueConverter {
  
  private static final ValueParser[] PARSERS = { 
    MULTILINE_STRING_PARSER, LITERAL_STRING_PARSER, STRING_PARSER, DATE_PARSER, INTEGER_PARSER, FLOAT_PARSER, BOOLEAN_PARSER, ARRAY_PARSER
  };

  public Object convert(String value) {
    for (ValueParser valueParser : PARSERS) {
      if (valueParser.canParse(value)) {
        return valueParser.parse(value);
      }
    }
    
    return INVALID;
  }
}

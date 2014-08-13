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

  public Object convert(String value) {
    if (MULTILINE_STRING_PARSER.canParse(value)) {
      return MULTILINE_STRING_PARSER.parse(value);
    } else if (LITERAL_STRING_PARSER.canParse(value)) {
      return LITERAL_STRING_PARSER.parse(value);
    } else if (STRING_PARSER.canParse(value)) {
      return STRING_PARSER.parse(value);
    } else if (INTEGER_PARSER.canParse(value)) {
      return INTEGER_PARSER.parse(value);
    } else if (FLOAT_PARSER.canParse(value)) {
      return FLOAT_PARSER.parse(value);
    } else if (BOOLEAN_PARSER.canParse(value)) {
      return BOOLEAN_PARSER.parse(value);
    } else if (ARRAY_PARSER.canParse(value)) {
      return ARRAY_PARSER.parse(value);
    } else if (DATE_PARSER.canParse(value)) {
      return DATE_PARSER.parse(value);
    } else {
      return INVALID;
    }
  }
}

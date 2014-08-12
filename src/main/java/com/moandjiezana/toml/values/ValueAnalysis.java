package com.moandjiezana.toml.values;

import static com.moandjiezana.toml.values.ArrayParser.ARRAY_PARSER;
import static com.moandjiezana.toml.values.BooleanParser.BOOLEAN_PARSER;
import static com.moandjiezana.toml.values.DateParser.DATE_PARSER;
import static com.moandjiezana.toml.values.FloatParser.FLOAT_PARSER;
import static com.moandjiezana.toml.values.IntegerParser.INTEGER_PARSER;
import static com.moandjiezana.toml.values.StringParser.STRING_PARSER;

public class ValueAnalysis {
  public static final Object INVALID = new Object();

  public Object convert(String value) {
    if (STRING_PARSER.canParse(value)) {
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

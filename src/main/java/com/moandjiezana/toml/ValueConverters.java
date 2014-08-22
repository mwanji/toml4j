package com.moandjiezana.toml;

import static com.moandjiezana.toml.ArrayConverter.ARRAY_PARSER;
import static com.moandjiezana.toml.BooleanConverter.BOOLEAN_PARSER;
import static com.moandjiezana.toml.DateConverter.DATE_PARSER;
import static com.moandjiezana.toml.FloatConverter.FLOAT_PARSER;
import static com.moandjiezana.toml.IntegerConverter.INTEGER_PARSER;
import static com.moandjiezana.toml.LiteralStringConverter.LITERAL_STRING_PARSER;
import static com.moandjiezana.toml.MultilineLiteralStringConverter.MULTILINE_LITERAL_STRING_CONVERTER;
import static com.moandjiezana.toml.MultilineStringConverter.MULTILINE_STRING_PARSER;
import static com.moandjiezana.toml.StringConverter.STRING_PARSER;
import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

class ValueConverters {
  
  private static final ValueConverter[] PARSERS = { 
    MULTILINE_STRING_PARSER, MULTILINE_LITERAL_STRING_CONVERTER, LITERAL_STRING_PARSER, STRING_PARSER, DATE_PARSER, INTEGER_PARSER, FLOAT_PARSER, BOOLEAN_PARSER, ARRAY_PARSER
  };

  public Object convert(String value) {
    for (ValueConverter valueParser : PARSERS) {
      if (valueParser.canConvert(value)) {
        return valueParser.convert(value);
      }
    }
    
    return INVALID;
  }
}

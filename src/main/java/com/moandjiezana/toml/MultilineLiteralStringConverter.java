package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import static com.moandjiezana.toml.ValueConverterUtils.parse;
import static com.moandjiezana.toml.ValueConverterUtils.parser;

import java.util.List;

class MultilineLiteralStringConverter implements ValueConverter {
  
  static final MultilineLiteralStringConverter MULTILINE_LITERAL_STRING_CONVERTER = new MultilineLiteralStringConverter(); 
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'''");
  }

  @Override
  public Object convert(String s) {
    List<String> result = parse(parser().MultilineLiteralString(), s);
    
    if (result == null) {
      return INVALID;
    }
    
    return result.get(0);
  }

  private MultilineLiteralStringConverter() {}
}

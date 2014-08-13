package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

class MultilineStringConverter implements ValueConverter {
  
  static final MultilineStringConverter MULTILINE_STRING_PARSER = new MultilineStringConverter();

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("\"\"\"");
  }

  @Override
  public Object convert(String s) {
    int terminator = s.indexOf("\"\"\"", 3);
    
    if (terminator == -1) {
      return INVALID;
    }
    
    if (!ValueConverterUtils.isComment(s.substring(terminator + 3))) {
      return INVALID;
    }
    
    s = s.substring(2, terminator + 1);
    s = s.replaceAll("\\\\\\s+", "");
    
    return StringConverter.STRING_PARSER.convert(s);
  }
  
  private MultilineStringConverter() {}

}

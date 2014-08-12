package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueParserUtils.INVALID;

class MultilineStringParser implements ValueParser {
  
  static final MultilineStringParser MULTILINE_STRING_PARSER = new MultilineStringParser();

  @Override
  public boolean canParse(String s) {
    return s.startsWith("\"\"\"");
  }

  @Override
  public Object parse(String s) {
    int terminator = s.indexOf("\"\"\"", 3);
    
    if (terminator == -1) {
      return INVALID;
    }
    
    if (!ValueParserUtils.isComment(s.substring(terminator + 3))) {
      return INVALID;
    }
    
    s = s.substring(2, terminator + 1);
    s = s.replaceAll("\\\\\\s+", "");
    
    return StringParser.STRING_PARSER.parse(s);
  }
  
  private MultilineStringParser() {}

}

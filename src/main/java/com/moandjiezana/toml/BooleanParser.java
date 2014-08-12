package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueParserUtils.INVALID;

class BooleanParser implements ValueParser {
  
  static final BooleanParser BOOLEAN_PARSER = new BooleanParser(); 

  @Override
  public boolean canParse(String s) {
    return s.startsWith("true") || s.startsWith("false");
  }

  @Override
  public Object parse(String s) {
    if (s.startsWith("true") && !ValueParserUtils.isComment(s.substring(4)) ||
        s.startsWith("false") && !ValueParserUtils.isComment(s.substring(5))) {
      return INVALID;
    }
    
    return s.startsWith("true") ? Boolean.TRUE : Boolean.FALSE;
  }

  private BooleanParser() {}
}

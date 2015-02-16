package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;


class LiteralStringConverter implements ValueConverter {

  static final LiteralStringConverter LITERAL_STRING_PARSER = new LiteralStringConverter();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'");
  }

  @Override
  public Object convert(String s, AtomicInteger index, Context context) {
    int startLine = context.line.get();
    boolean terminated = false;
    int startIndex = index.incrementAndGet();
    
    for (int i = index.get(); i < s.length(); i = index.incrementAndGet()) {
      char c = s.charAt(i);
      
      if (c == '\'') {
        terminated = true;
        break;
      }
    }
    
    if (!terminated) {
      Results.Errors errors = new Results.Errors();
      errors.unterminated(context.identifier.getName(), s.substring(startIndex), startLine);
      return errors;
    }
    
    String substring = s.substring(startIndex, index.get());
    
    return substring;
  }

  private LiteralStringConverter() {}
}

package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class IdentifierConverter {
  
  static final IdentifierConverter IDENTIFIER_CONVERTER = new IdentifierConverter();

  Identifier convert(char[] chars, AtomicInteger index) {
    boolean quoted = false;
    StringBuilder name = new StringBuilder();
    Identifier identifier = null;
    
    for (int i = index.get(); i < chars.length; i = index.incrementAndGet()) {
      char c = chars[i];
      if (c == '"' && (i == 0 || chars[i - 1] != '\\')) {
        quoted = !quoted;
        name.append('"');
      } else if (c == '\n' || (!quoted && (c == '#' || c == '='))) {
        return new Identifier(name.toString().trim());
      } else if (i == chars.length - 1 && identifier == null) {
        name.append(c);
        return new Identifier(name.toString().trim());
      } else {
        name.append(c);
      }
    }
    
    return identifier != null ? identifier : Identifier.INVALID;
  }
  
  private IdentifierConverter() {}
}

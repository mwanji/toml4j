package de.thelooter.toml;

import java.util.concurrent.atomic.AtomicInteger;

class MultilineLiteralStringValueReader implements ValueReader {
  
  static final MultilineLiteralStringValueReader MULTILINE_LITERAL_STRING_VALUE_READER = new MultilineLiteralStringValueReader(); 
  
  @Override
  public boolean canRead(String s) {
    return s.startsWith("'''");
  }

  @Override
  public Object read(String s, AtomicInteger index, Context context) {
    AtomicInteger line = context.line;
    int startLine = line.get();
    int originalStartIndex = index.get();
    int startIndex = index.addAndGet(3);
    int endIndex = -1;
    
    if (s.charAt(startIndex) == '\n') {
      startIndex = index.incrementAndGet();
      line.incrementAndGet();
    }
    
    for (int i = startIndex; i < s.length(); i = index.incrementAndGet()) {
      char c = s.charAt(i);

      if (c == '\n') {
        line.incrementAndGet();
      }
      
      if (c == '\'' && s.length() > i + 2 && s.charAt(i + 1) == '\'' && s.charAt(i + 2) == '\'') {
        endIndex = i;
        index.addAndGet(2);
        break;
      }
    }
    
    if (endIndex == -1) {
      Results.Errors errors = new Results.Errors();
      errors.unterminated(context.identifier.getName(), s.substring(originalStartIndex), startLine);
      return errors;
    }

    return s.substring(startIndex, endIndex);
  }

  private MultilineLiteralStringValueReader() {}
}

package com.moandjiezana.toml;

import com.moandjiezana.toml.Keys.Quote;

class Identifier {
  
  static final Identifier INVALID = new Identifier("", null);
  
  private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-";

  private final String name;
  private final Type type;
  
  static Identifier from(String name, Context context) {
    Type type;
    boolean valid;
    name = name.trim();
    if (name.startsWith("[[")) {
      type = Type.TABLE_ARRAY;
      valid = isValidTableArray(name, context);
    } else if (name.startsWith("[")) {
      type = Type.TABLE;
      valid = isValidTable(name, context);
    } else {
      type = Type.KEY;
      valid = isValidKey(name, context);
    }
    
    if (!valid) {
      return Identifier.INVALID;
    }
    
    return new Identifier(extractName(name), type);
  }

  private Identifier(String name, Type type) {
    this.name = name;
    this.type = type;
  }
  
  String getName() {
    return name;
  }
  
  String getBareName() {
    if (isKey()) {
      return name;
    }
    
    if (isTable()) {
      return name.substring(1, name.length() - 1);
    }
    
    return name.substring(2, name.length() - 2);
  }
  
  boolean isKey() {
    return type == Type.KEY;
  }
  
  boolean isTable() {
    return type == Type.TABLE;
  }
  
  boolean isTableArray() {
    return type == Type.TABLE_ARRAY;
  }
  
  private static enum Type {
    KEY, TABLE, TABLE_ARRAY;
  }
  
  private static String extractName(String raw) {
    Quote quote = Quote.NONE;
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < raw.length(); i++) {
      char c = raw.charAt(i);
      if (c == '"' && (i == 0 || raw.charAt(i - 1) != '\\')) {
        if (quote != Quote.NONE && quote.getChar() == c) {
          quote = Quote.NONE;
        } else {
          quote = Quote.quoteFromChar(c);
        }  
        sb.append('"');
      } else if (quote != Quote.NONE || !Character.isWhitespace(c)) {
        sb.append(c);
      }
    }
    
    return StringValueReaderWriter.STRING_VALUE_READER_WRITER.replaceUnicodeCharacters(sb.toString());
  }
  
  private static boolean isValidKey(String name, Context context) {
    if (name.trim().isEmpty()) {
      context.errors.invalidKey(name, context.line.get());
      return false;
    }
    
    Quote quote = Quote.NONE;
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      
      if (c == '"' && (i == 0 || name.charAt(i - 1) != '\\')) {
        if (quote == Quote.NONE && i > 0 && name.charAt(i - 1) != '.') {
          context.errors.invalidKey(name, context.line.get());
          return false;
        }
        if (quote != Quote.NONE && quote.getChar() == c) {
          quote = Quote.NONE;
        } else {
          quote = Quote.quoteFromChar(c);
        } 
      } else if (quote == Quote.NONE && (ALLOWED_CHARS.indexOf(c) == -1)) {
        context.errors.invalidKey(name, context.line.get());
        return false;
      }
    }
    
    return true;
  }
  
  private static boolean isValidTable(String name, Context context) {
    boolean valid = true;
    
    if (!name.endsWith("]")) {
      valid = false;
    }
    
    String trimmed = name.substring(1, name.length() - 1).trim();
    if (trimmed.isEmpty() || trimmed.charAt(0) == '.' || trimmed.endsWith(".")) {
      valid = false;
    }
    
    if (!valid) {
      context.errors.invalidTable(name, context.line.get());
      return false;
    }
    
    Quote quote = Quote.NONE;
    boolean dotAllowed = false;
    boolean quoteAllowed = true;
    boolean charAllowed = true;
    
    for (int i = 0; i < trimmed.length(); i++) {
      char c = trimmed.charAt(i);
      
      if (!valid) {
        break;
      }
      
      if (Keys.isQuote(c)) {
        if (!quoteAllowed) {
          valid = false;
        } else if (quote != Quote.NONE && quote.getChar() == c && trimmed.charAt(i - 1) != '\\') {
          charAllowed = false;
          dotAllowed = true;
          quoteAllowed = false;
        } else if (quote == Quote.NONE) {
          quote = Quote.quoteFromChar(c);
          quoteAllowed = true;
        }
      } else if (quote != Quote.NONE && quote.getChar() != c) {
        continue;
      } else if (c == '.') {
        if (dotAllowed) {
          charAllowed = true;
          dotAllowed = false;
          quoteAllowed = true;
        } else {
          context.errors.emptyImplicitTable(name, context.line.get());
          return false;
        }
      } else if (Character.isWhitespace(c)) {
        char prev = trimmed.charAt(i - 1);
        if (!Character.isWhitespace(prev) && prev != '.') {
          charAllowed = false;
          dotAllowed = true;
          quoteAllowed = true;
        }
      } else {
        if (charAllowed && ALLOWED_CHARS.indexOf(c) > -1) {
          charAllowed = true;
          dotAllowed = true;
          quoteAllowed = false;
        } else {
          valid = false;
        }
      }
    }
    
    if (!valid) {
      context.errors.invalidTable(name, context.line.get());
      return false;
    }
    
    return true;
  }

  private static boolean isValidTableArray(String line, Context context) {
    boolean valid = true;
    
    if (!line.endsWith("]]")) {
      valid = false;
    }
    
    String trimmed = line.substring(2, line.length() - 2).trim();
    if (trimmed.isEmpty() || trimmed.charAt(0) == '.' || trimmed.endsWith(".")) {
      valid = false;
    }
    
    if (!valid) {
      context.errors.invalidTableArray(line, context.line.get());
      return false;
    }

    boolean quoted = false;
    boolean dotAllowed = false;
    boolean quoteAllowed = true;
    boolean charAllowed = true;
    
    for (int i = 0; i < trimmed.length(); i++) {
      char c = trimmed.charAt(i);
      
      if (!valid) {
        break;
      }
      
      if (c == '"') {
        if (!quoteAllowed) {
          valid = false;
        } else if (quoted && trimmed.charAt(i - 1) != '\\') {
          charAllowed = false;
          dotAllowed = true;
          quoteAllowed = false;
        } else if (!quoted) {
          quoted = true;
          quoteAllowed = true;
        }
      } else if (quoted) {
        continue;
      } else if (c == '.') {
        if (dotAllowed) {
          charAllowed = true;
          dotAllowed = false;
          quoteAllowed = true;
        } else {
          context.errors.emptyImplicitTable(line, context.line.get());
          return false;
        }
      } else if (Character.isWhitespace(c)) {
        char prev = trimmed.charAt(i - 1);
        if (!Character.isWhitespace(prev) && prev != '.' && prev != '"') {
          charAllowed = false;
          dotAllowed = true;
          quoteAllowed = true;
        }
      } else {
        if (charAllowed && ALLOWED_CHARS.indexOf(c) > -1) {
          charAllowed = true;
          dotAllowed = true;
          quoteAllowed = false;
        } else {
          valid = false;
        }
      }
    }
    
    if (!valid) {
      context.errors.invalidTableArray(line, context.line.get());
      return false;
    }
    
    return true;
  }
}

package com.moandjiezana.toml;

class Identifier {
  
  static final Identifier INVALID = new Identifier("", null);
  
  private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-.";
  private static final String ALLOWED_CHARS_KEYS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-";

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
    boolean quoted = false;
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < raw.length(); i++) {
      char c = raw.charAt(i);
      if (c == '"' && (i == 0 || raw.charAt(i - 1) != '\\')) {
        quoted = !quoted;
        sb.append('"');
      } else if (quoted || !Character.isWhitespace(c)) {
        sb.append(c);
      }
    }
    
    return sb.toString();
  }
  
  private static boolean isValidKey(String name, Context context) {
    if (name.trim().isEmpty()) {
      context.errors.invalidKey(name, context.line.get());
      return false;
    }
    
    boolean quoted = false;
    char[] chars = name.toCharArray();
    
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      
      if (c == '"' && (i == 0 || chars[i - 1] != '\\')) {
        if (!quoted && i > 0 && chars [i - 1] != '.') {
          context.errors.invalidKey(name, context.line.get());
          return false;
        }
        quoted = !quoted;
      } else if (!quoted && (ALLOWED_CHARS_KEYS.indexOf(c) == -1)) {
        context.errors.invalidKey(name, context.line.get());
        return false;
      }
    }
    
    return true;
  }
  
  private static boolean isValidTable(String name, Context context) {
    if (!name.endsWith("]")) {
      context.errors.invalidTable(name, context.line.get());
      return false;
    }
    
    char[] chars = name.toCharArray();
    boolean quoted = false;
    boolean terminated = false;
    int endIndex = -1;
    boolean preKey = true;
    boolean valid = true;
    
    for (int i = 1; i < name.length() - 1; i++) {
      char c = name.charAt(i);
      if (c == '"' && chars[i - 1] != '\\') {
        if (!quoted && i > 1 && chars [i - 1] != '.' && !Character.isWhitespace(chars[i - 1])) {
          valid = false;
          break;
        }
        quoted = !quoted;
      } else if (!quoted && c == '.') {
        preKey = true;
      } else if (!quoted && Character.isWhitespace(c)) {
        if (preKey && i > 1 && chars[i - 1] != '.' && !Character.isWhitespace(chars[i - 1])) {
          valid = false;
          break;
        }
        if (!preKey && chars.length > i + 1 && chars[i + 1] != '.' && chars[i + 1] != ']' && !Character.isWhitespace(chars[i + 1])) {
          valid = false;
          break;
        }
      } else if (!quoted && (ALLOWED_CHARS.indexOf(c) == -1)) {
        valid = false;
        break;
      } else if (!quoted) {
        preKey = false;
      }
    }
    
    if (!valid) {
      context.errors.invalidTable(name, context.line.get());
      return false;
    }
    
//    return StringConverter.STRING_PARSER.replaceUnicodeCharacters(tableName);
    return true;
  }

  private static boolean isValidTableArray(String line, Context context) {
    if (!line.endsWith("]]") || line.substring(2, line.length() - 2).trim().isEmpty()) {
      context.errors.invalidTableArray(line, context.line.get());
      
      return false;
    }
    
    char[] chars = line.toCharArray();
    boolean quoted = false;
    boolean preKey = true;
    boolean valid = true;
    
    for (int i = 2; i < line.length() - 2; i++) {
      char c = chars[i];
      if (c == '"' && chars[i - 1] != '\\') {
        if (!quoted && i > 1 && chars [i - 1] != '.' && !Character.isWhitespace(chars[i - 1])) {
          valid = false;
        }
        quoted = !quoted;
      } else if (!quoted && c == '.') {
        preKey = true;
      } else if (!quoted && Character.isWhitespace(c)) {
        if (preKey && i > 2 && chars[i - 1] != '.' && !Character.isWhitespace(chars[i - 1])) {
          valid = false;
        }
        if (!preKey && chars.length > i + 1 && chars[i + 1] != '.' && chars[i + 1] != ']' && !Character.isWhitespace(chars[i + 1])) {
          valid = false;
          break;
        }
      } else if (!quoted && (ALLOWED_CHARS.indexOf(c) == -1)) {
        valid = false;
      } else if (!valid) {
        break;
      } else {
        preKey = false;
      }
    }
    
    if (!valid) {
      context.errors.invalidTableArray(line, context.line.get());
    }
    
    return valid;
  }
}

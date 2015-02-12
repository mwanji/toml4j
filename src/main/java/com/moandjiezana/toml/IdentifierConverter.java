package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class IdentifierConverter {
  
  static final IdentifierConverter IDENTIFIER_CONVERTER = new IdentifierConverter();

  private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-.";

  Identifier convert(char[] chars, AtomicInteger index, Context context) {
    boolean quoted = false;
    StringBuilder name = new StringBuilder();
    Identifier identifier = null;
    boolean terminated = false;
    boolean isKey = chars[index.get()] != '[';
    boolean isTableArray = !isKey && chars.length > index.get() + 1 && chars[index.get() + 1] == '[';
    boolean inComment = false;
    
    for (int i = index.get(); i < chars.length; i = index.incrementAndGet()) {
      char c = chars[i];
      if (c == '"' && (i == 0 || chars[i - 1] != '\\')) {
        quoted = !quoted;
        name.append('"');
      } else if (c == '\n') {
        break;
      } else if (quoted) {
        name.append(c);
      } else if (c == '=' && isKey) {
        terminated = true;
        break;
      } else if (c == ']' && !isKey) {
        if (!isTableArray || chars.length > index.get() + 1 && chars[index.get() + 1] == ']') {
          terminated = true;
          name.append(']');
          if (isTableArray) {
            name.append(']');
          }
        }
      } else if (terminated && c == '#') {
        inComment = true;
      } else if (terminated && !Character.isWhitespace(c) && !inComment) {
        terminated = false;
        break;
      } else if (!terminated) {
        name.append(c);
      }
    }
    
    if (!terminated) {
      if (isKey) {
        context.errors.unterminatedKey(name.toString(), context.line.get());
      } else {
        context.errors.invalidKey(name.toString(), context.line.get());
      }
      
      return Identifier.INVALID;
    }
    
    return Identifier.from(name.toString(), context);
  }
  
  private IdentifierConverter() {}
  
  private static String getKey(String key) {
    key = key.trim();
    
    if (key.isEmpty()) {
      return null;
    }
    
    boolean quoted = false;
    char[] chars = key.toCharArray();
    StringBuilder sb = new StringBuilder(key.length());
    
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      
      if (c == '"' && (i == 0 || chars[i - 1] != '\\')) {
        if (!quoted && i > 0 && chars [i - 1] != '.') {
          return null;
        }
        quoted = !quoted;
      } else if (!quoted && (ALLOWED_CHARS.indexOf(c) == -1)) {
        return null;
      }
      
      sb.append(c);
    }
    
    return sb.toString();
  }

  private static String getTableArrayName(String line) {
    StringBuilder sb = new StringBuilder();
    char[] chars = line.toCharArray();
    boolean quoted = false;
    boolean terminated = false;
    int endIndex = -1;
    boolean preKey = true;
    
    for (int i = 2; i < chars.length; i++) {
      char c = chars[i];
      if (c == '"' && chars[i - 1] != '\\') {
        if (!quoted && i > 1 && chars [i - 1] != '.' && !Character.isWhitespace(chars[i - 1])) {
          break;
        }
        quoted = !quoted;
      } else if (!quoted && c == ']') {
        if (chars.length > i + 1 && chars[i + 1] == ']') {
          terminated = true;
          endIndex = i + 1;
          break;
        }
      } else if (!quoted && c == '.') {
        preKey = true;
      } else if (!quoted && Character.isWhitespace(c)) {
        if (preKey && i > 2 && chars[i - 1] != '.' && !Character.isWhitespace(chars[i - 1])) {
          break;
        }
        if (!preKey && chars.length > i + 1 && chars[i + 1] != '.' && chars[i + 1] != ']' && !Character.isWhitespace(chars[i + 1])) {
          break;
        }
        continue;
      } else if (!quoted && (ALLOWED_CHARS.indexOf(c) == -1)) {
        break;
      } else {
        preKey = false;
      }
      
      sb.append(c);
    }
    
    if (!terminated || sb.length() == 0) {
      return null;
    }
    
    String tableName = sb.insert(0, "[[").append("]]").toString();
    
    return StringConverter.STRING_PARSER.replaceUnicodeCharacters(tableName);
  }
  
  private static String getTableName(String line) {
    StringBuilder sb = new StringBuilder();
    char[] chars = line.toCharArray();
    boolean quoted = false;
    boolean terminated = false;
    int endIndex = -1;
    boolean preKey = true;
    
    for (int i = 1; i < chars.length; i++) {
      char c = chars[i];
      if (c == '"' && chars[i - 1] != '\\') {
        if (!quoted && i > 1 && chars [i - 1] != '.' && !Character.isWhitespace(chars[i - 1])) {
          break;
        }
        quoted = !quoted;
      } else if (!quoted && c == ']') {
        terminated = true;
        endIndex = i;
        break;
      } else if (!quoted && c == '.') {
        preKey = true;
      } else if (!quoted && Character.isWhitespace(c)) {
        if (preKey && i > 1 && chars[i - 1] != '.' && !Character.isWhitespace(chars[i - 1])) {
          break;
        }
        if (!preKey && chars.length > i + 1 && chars[i + 1] != '.' && chars[i + 1] != ']' && !Character.isWhitespace(chars[i + 1])) {
          break;
        }
        continue;
      } else if (!quoted && (ALLOWED_CHARS.indexOf(c) == -1)) {
        break;
      } else if (!quoted) {
        preKey = false;
      }
      
      sb.append(c);
    }
    
    if (!terminated) {
      return null;
    }
    
    sb.insert(0, '[').append(']');
    String tableName = sb.toString();
    
    return StringConverter.STRING_PARSER.replaceUnicodeCharacters(tableName);
  }

}

package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.isComment;

import java.util.ArrayList;
import java.util.List;

class Keys {
  
  private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-.";
  
  static class Key {
    final String name;
    final int index;
    final String path;

    Key(String name, int index, Key next) {
      this.name = name;
      this.index = index;
      if (next != null) {
        this.path = name + "." + next.path;
      } else {
        this.path = name;
      }
    }
  }

  static String getKey(String key) {
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

  static Keys.Key[] split(String key) {
    List<Key> splitKey = new ArrayList<Key>();
    StringBuilder current = new StringBuilder();
    char[] chars = key.toCharArray();
    boolean quoted = false;
    boolean indexable = true;
    boolean inIndex = false;
    int index = -1;
    
    for (int i = chars.length - 1; i > -1; i--) {
      char c = chars[i];
      if (c == ']' && indexable) {
        inIndex = true;
        continue;
      }
      indexable = false;
      if (c == '[' && inIndex) {
        inIndex = false;
        index = Integer.parseInt(current.toString());
        current = new StringBuilder();
        continue;
      }
      if (c == '"' && (i == 0 || chars[i - 1] != '\\')) {
        quoted = !quoted;
        indexable = false;
      }
      if (c != '.' || quoted) {
        current.insert(0, c);
      } else {
        splitKey.add(0, new Key(current.toString(), index, !splitKey.isEmpty() ? splitKey.get(0) : null));
        indexable = true;
        index = -1;
        current = new StringBuilder();
      }
    }
    
    splitKey.add(0, new Key(current.toString(), index, !splitKey.isEmpty() ? splitKey.get(0) : null));
    
    return splitKey.toArray(new Key[0]);
  }

  static String getTableArrayName(String line) {
    StringBuilder sb = new StringBuilder();
    char[] chars = line.toCharArray();
    boolean quoted = false;
    boolean terminated = false;
    
    for (int i = 2; i < chars.length; i++) {
      char c = chars[i];
      if (c == '"' && chars[i - 1] != '\\') {
        quoted = !quoted;
      } else if (!quoted && c == ']') {
        if (chars.length > i + 1 && chars[i + 1] == ']') {
          terminated = true;
          break;
        }
      } else if (!quoted && (ALLOWED_CHARS.indexOf(c) == -1)) {
        break;
      }
      
      sb.append(c);
    }
    
    String tableName = sb.toString();
    
    if (!terminated || tableName.isEmpty() || !isComment(line.substring(tableName.length() + 4))) {
      return null;
    }
    
    tableName = StringConverter.STRING_PARSER.replaceUnicodeCharacters(tableName);
    return tableName;
  }
  
  /**
   * @param line trimmed TOML line to parse
   * @return null if line is not a valid table identifier
   */
  static String getTableName(String line) {
    StringBuilder sb = new StringBuilder();
    char[] chars = line.toCharArray();
    boolean quoted = false;
    boolean terminated = false;
    
    for (int i = 1; i < chars.length; i++) {
      char c = chars[i];
      if (c == '"' && chars[i - 1] != '\\') {
        if (!quoted && i > 1 && chars [i - 1] != '.') {
          break;
        }
        quoted = !quoted;
      } else if (!quoted && c == ']') {
        terminated = true;
        break;
      } else if (!quoted && (ALLOWED_CHARS.indexOf(c) == -1)) {
        break;
      }
      
      sb.append(c);
    }
    
    String tableName = sb.toString();
    
    if (!terminated || !isComment(line.substring(tableName.length() + 2))) {
      return null;
    }
    
    tableName = StringConverter.STRING_PARSER.replaceUnicodeCharacters(tableName);
    return tableName;
  }
  
  private Keys() {}
}

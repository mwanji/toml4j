package com.moandjiezana.toml;

class Identifier {
  
  static final Identifier INVALID = new Identifier("");
  
  private final String name;
  private final Type type;

  Identifier(String name) {
    this.name = name;
    if (name.startsWith("[[")) {
      this.type = Type.TABLE_ARRAY;
    } else if (name.startsWith("[")) {
      this.type = Type.TABLE;
    } else {
      this.type = Type.KEY;
    }
  }
  
  boolean acceptsNext(char c) {
    if (isKey()) {
      return c == '=';
    }
    
    return c == '\n' || c == '#';
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
  
  boolean isValid() {
    if (isKey()) {
      return Keys.getKey(name) != null;
    }
    
    if (isTable()) {
      return Keys.getTableName(name) != null;
    }
    
    return Keys.getTableArrayName(name) != null;
  }
  
  private static enum Type {
    KEY, TABLE, TABLE_ARRAY;
  }
}

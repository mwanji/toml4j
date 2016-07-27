package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.List;

class Keys {
  
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

  static Keys.Key[] split(String key) {
    List<Key> splitKey = new ArrayList<Key>();
    StringBuilder current = new StringBuilder();
    boolean quoted = false;
    boolean indexable = true;
    boolean inIndex = false;
    int index = -1;
    
    for (int i = key.length() - 1; i > -1; i--) {
      char c = key.charAt(i);
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
      if (isQuote(c) && (i == 0 || key.charAt(i - 1) != '\\')) {
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
  
  static boolean isQuote(char c) {
    return c == '"' || c == '\'';
  }

  private Keys() {}
}

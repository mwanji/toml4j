package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.List;

class Keys {
  
  static String[] split(String key) {
    List<String> splitKey = new ArrayList<String>();
    StringBuilder current = new StringBuilder();
    char[] chars = key.toCharArray();
    boolean quoted = false;
    
    for (char c : chars) {
      if (c == '"') {
        quoted = !quoted;
      }
      if (c != '.' || quoted) {
        current.append(c);
      } else {
        splitKey.add(current.toString());
        current = new StringBuilder();
      }
    }
    
    splitKey.add(current.toString());
    
    return splitKey.toArray(new String[0]);
  }
  
  private Keys() {}

}

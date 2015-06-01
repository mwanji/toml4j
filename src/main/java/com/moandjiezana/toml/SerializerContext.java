package com.moandjiezana.toml;

class SerializerContext {
  private String key = "";
  private boolean isArrayOfTable = false;
  StringBuilder serialized = new StringBuilder();

  SerializerContext(String key, StringBuilder serialized) {
    this.key = key;
    this.serialized = serialized;
  }

  SerializerContext() {
  }

  SerializerContext extend(String newKey) {
    String fullKey = key + (key.isEmpty() ? newKey : "." + newKey);

    return new SerializerContext(fullKey, serialized);
  }

  SerializerContext extend() {
    return new SerializerContext(key, serialized);
  }

  void serializeKey() {
    if (key.isEmpty()) {
      return;
    }

    if (serialized.length() > 0) {
      serialized.append('\n');
    }

    if (isArrayOfTable) {
      serialized.append("[[").append(key).append("]]\n");
    } else {
      serialized.append('[').append(key).append("]\n");
    }
  }

  void indent() {
    serialized.append(key.isEmpty() ? "" : "  ");
  }

  SerializerContext setIsArrayOfTable(boolean isArrayOfTable) {
    this.isArrayOfTable = isArrayOfTable;
    return this;
  }
}

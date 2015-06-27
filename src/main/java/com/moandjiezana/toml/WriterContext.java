package com.moandjiezana.toml;

class WriterContext {
  private String key = "";
  private boolean isArrayOfTable = false;
  StringBuilder output = new StringBuilder();

  WriterContext(String key, StringBuilder output) {
    this.key = key;
    this.output = output;
  }

  WriterContext() {
  }

  WriterContext extend(String newKey) {
    String fullKey = key + (key.isEmpty() ? newKey : "." + newKey);

    return new WriterContext(fullKey, output);
  }

  WriterContext extend() {
    return new WriterContext(key, output);
  }

  void writeKey() {
    if (key.isEmpty()) {
      return;
    }

    if (output.length() > 0) {
      output.append('\n');
    }

    if (isArrayOfTable) {
      output.append("[[").append(key).append("]]\n");
    } else {
      output.append('[').append(key).append("]\n");
    }
  }

  void indent() {
    output.append(key.isEmpty() ? "" : "  ");
  }

  WriterContext setIsArrayOfTable(boolean isArrayOfTable) {
    this.isArrayOfTable = isArrayOfTable;
    return this;
  }
}

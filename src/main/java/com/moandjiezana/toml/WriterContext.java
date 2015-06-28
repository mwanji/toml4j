package com.moandjiezana.toml;

import java.util.Arrays;

class WriterContext {
  private String key = "";
  private String currentTableIndent = "";
  private String currentFieldIndent = "";
  private boolean isArrayOfTable = false;
  private final TomlWriter tomlWriter;
  StringBuilder output = new StringBuilder();

  WriterContext(String key, String tableIndent, StringBuilder output, TomlWriter tomlWriter) {
    this.key = key;
    this.currentTableIndent = tableIndent;
    this.currentFieldIndent = tableIndent + fillStringWithSpaces(tomlWriter.getIndentationPolicy().getKeyValueIndent());
    this.output = output;
    this.tomlWriter = tomlWriter;
  }

  WriterContext(TomlWriter tomlWriter) {
    this.tomlWriter = tomlWriter;
  }

  WriterContext pushTable(String newKey) {
    String newIndent = "";
    if (!key.isEmpty()) {
      newIndent = growIndent(tomlWriter.getIndentationPolicy());
    }

    String fullKey = key + (key.isEmpty() ? newKey : "." + newKey);

    return new WriterContext(fullKey, newIndent, output, tomlWriter);
  }

  WriterContext pushTableFromArray() {
    WriterContext subContext = new WriterContext(key, currentTableIndent, output, tomlWriter);
    subContext.setIsArrayOfTable(true);

    return subContext;
  }

  void writeKey() {
    if (key.isEmpty()) {
      return;
    }

    if (output.length() > 0) {
      output.append('\n');
    }

    output.append(currentTableIndent);

    if (isArrayOfTable) {
      output.append("[[").append(key).append("]]\n");
    } else {
      output.append('[').append(key).append("]\n");
    }
  }

  void indent() {
    if (!key.isEmpty()) {
      output.append(currentFieldIndent);
    }
  }

  WriterContext setIsArrayOfTable(boolean isArrayOfTable) {
    this.isArrayOfTable = isArrayOfTable;
    return this;
  }

  private String growIndent(WriterIndentationPolicy indentationPolicy) {
    return currentTableIndent + fillStringWithSpaces(indentationPolicy.getTableIndent());
  }

  private String fillStringWithSpaces(int count) {
    char[] chars = new char[count];
    Arrays.fill(chars, ' ');

    return new String(chars);
  }
}

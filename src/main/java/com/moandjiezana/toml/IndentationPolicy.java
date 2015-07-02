package com.moandjiezana.toml;

/**
 * Controls how a {@link TomlWriter} indents tables and key/value pairs.
 *
 * The default policy is to not indent.
 */
class IndentationPolicy {
  private final int tableIndent;
  private final int keyValueIndent;
  private final int arrayDelimiterPadding;

  IndentationPolicy(int keyIndentation, int tableIndentation, int arrayDelimiterPadding) {
    this.keyValueIndent = keyIndentation;
    this.tableIndent = tableIndentation;
    this.arrayDelimiterPadding = arrayDelimiterPadding;
  }

  int getTableIndent() {
    return tableIndent;
  }

  int getKeyValueIndent() {
    return keyValueIndent;
  }

  int getArrayDelimiterPadding() {
    return arrayDelimiterPadding;
  }
}

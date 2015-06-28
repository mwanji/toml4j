package com.moandjiezana.toml;

/**
 * Controls how a {@link TomlWriter} indents tables and key/value pairs.
 *
 * The default policy is to not indent.
 */
public class WriterIndentationPolicy {
  private int tableIndent = 0;
  private int keyValueIndent = 0;

  public int getTableIndent() {
    return tableIndent;
  }

  /**
   * Sets the number of spaces a nested table name is indented.
   *
   * @param tableIndent number of spaces to indent
   * @return this WriterIndentationPolicy instance
   */
  public WriterIndentationPolicy setTableIndent(int tableIndent) {
    this.tableIndent = tableIndent;
    return this;
  }

  public int getKeyValueIndent() {
    return keyValueIndent;
  }

  /**
   * Sets the number of spaces key/value pairs within a table are indented.
   *
   * @param keyValueIndent number of spaces to indent
   * @return this WriterIndentationPolicy instance
   */
  public WriterIndentationPolicy setKeyValueIndent(int keyValueIndent) {
    this.keyValueIndent = keyValueIndent;
    return this;
  }
}

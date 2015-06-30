package com.moandjiezana.toml;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

class WriterContext {
  private String key = "";
  private String currentTableIndent = "";
  private String currentFieldIndent = "";
  private String arrayKey = null;
  private boolean isArrayOfTable = false;
  private boolean empty = true;
  private final TomlWriter tomlWriter;
  private final Writer output;

  WriterContext(TomlWriter tomlWriter, Writer output) {
    this.tomlWriter = tomlWriter;
    this.output = output;
  }

  WriterContext pushTable(String newKey) {
    String newIndent = "";
    if (!key.isEmpty()) {
      newIndent = growIndent(tomlWriter.getIndentationPolicy());
    }

    String fullKey = key + (key.isEmpty() ? newKey : "." + newKey);

    WriterContext subContext = new WriterContext(fullKey, newIndent, output, tomlWriter);
    if (!empty) {
      subContext.empty = false;
    }
    
    return subContext;
  }

  WriterContext pushTableFromArray() {
    WriterContext subContext = new WriterContext(key, currentTableIndent, output, tomlWriter);
    if (!empty) {
      subContext.empty = false;
    }
    subContext.setIsArrayOfTable(true);

    return subContext;
  }
  
  WriterContext write(String s) {
    try {
      output.write(s);
      if (s != null && !s.isEmpty()) {
        empty = false;
      }
      
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void write(char[] chars) {
    for (char c : chars) {
      write(c);
    }
  }
  
  WriterContext write(char c) {
    try {
      output.write(c);
      empty = false;
      
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void writeKey() {
    if (key.isEmpty()) {
      return;
    }

    if (!empty) {
      write('\n');
    }

    write(currentTableIndent);

    if (isArrayOfTable) {
      write("[[").write(key).write("]]\n");
    } else {
      write('[').write(key).write("]\n");
    }
  }

  void writeArrayDelimiterPadding() {
    for (int i = 0; i < tomlWriter.getIndentationPolicy().getArrayDelimiterPadding(); i++) {
      write(' ');
    }
  }

  void indent() {
    if (!key.isEmpty()) {
      write(currentFieldIndent);
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

  private WriterContext(String key, String tableIndent, Writer output, TomlWriter tomlWriter) {
    this.key = key;
    this.currentTableIndent = tableIndent;
    this.currentFieldIndent = tableIndent + fillStringWithSpaces(tomlWriter.getIndentationPolicy().getKeyValueIndent());
    this.output = output;
    this.tomlWriter = tomlWriter;
  }

  public TomlWriter getTomlWriter() {
    return tomlWriter;
  }

  public WriterContext setArrayKey(String arrayKey) {
    this.arrayKey = arrayKey;
    return this;
  }

  public String getContextPath() {
    return key.isEmpty() ? arrayKey : key + "." + arrayKey;
  }
}

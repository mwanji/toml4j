package com.moandjiezana.toml;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

class WriterContext {
  private String arrayKey = null;
  private boolean isArrayOfTable = false;
  private boolean empty = true;
  private final String key;
  private final String currentTableIndent;
  private final String currentFieldIndent;
  private final Writer output;
  private final IndentationPolicy indentationPolicy;
  private final DatePolicy datePolicy;

  WriterContext(IndentationPolicy indentationPolicy, DatePolicy datePolicy, Writer output) {
    this("", "", output, indentationPolicy, datePolicy);
  }

  WriterContext pushTable(String newKey) {
    String newIndent = "";
    if (!key.isEmpty()) {
      newIndent = growIndent(indentationPolicy);
    }

    String fullKey = key.isEmpty() ? newKey : key + "." + newKey;

    WriterContext subContext = new WriterContext(fullKey, newIndent, output, indentationPolicy, datePolicy);
    if (!empty) {
      subContext.empty = false;
    }
    empty = false;

    return subContext;
  }

  WriterContext pushTableFromArray() {
    WriterContext subContext = new WriterContext(key, currentTableIndent, output, indentationPolicy, datePolicy);
    if (!empty) {
      subContext.empty = false;
    }
    subContext.setIsArrayOfTable(true);
    empty = false;

    return subContext;
  }
  
  WriterContext write(String s) {
    try {
      output.write(s);
      if (empty && !s.isEmpty()) {
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
    for (int i = 0; i < indentationPolicy.getArrayDelimiterPadding(); i++) {
      write(' ');
    }
  }

  void indent() {
    if (!key.isEmpty()) {
      write(currentFieldIndent);
    }
  }
  
  DatePolicy getDatePolicy() {
    return datePolicy;
  }

  WriterContext setIsArrayOfTable(boolean isArrayOfTable) {
    this.isArrayOfTable = isArrayOfTable;
    return this;
  }

  WriterContext setArrayKey(String arrayKey) {
    this.arrayKey = arrayKey;
    return this;
  }

  String getContextPath() {
    return key.isEmpty() ? arrayKey : key + "." + arrayKey;
  }

  private String growIndent(IndentationPolicy indentationPolicy) {
    return currentTableIndent + fillStringWithSpaces(indentationPolicy.getTableIndent());
  }

  private String fillStringWithSpaces(int count) {
    char[] chars = new char[count];
    Arrays.fill(chars, ' ');

    return new String(chars);
  }

  private WriterContext(String key, String tableIndent, Writer output, IndentationPolicy indentationPolicy, DatePolicy datePolicy) {
    this.key = key;
    this.output = output;
    this.indentationPolicy = indentationPolicy;
    this.currentTableIndent = tableIndent;
    this.datePolicy = datePolicy;
    this.currentFieldIndent = tableIndent + fillStringWithSpaces(this.indentationPolicy.getKeyValueIndent());
  }
}

package com.moandjiezana.toml;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

class WriterContext {
  private String arrayKey = null;
  private boolean isArrayOfTable = false;
  private boolean empty = true;
  private final String key;
  private final String currentTableIndent;
  private final String currentFieldIndent;
  private final Writer output;
  private final WriterIndentationPolicy indentationPolicy;
  private final TimeZone timeZone;

  WriterContext(WriterIndentationPolicy indentationPolicy, TimeZone timeZone, Writer output) {
    this("", "", output, indentationPolicy, timeZone);
  }

  WriterContext pushTable(String newKey) {
    String newIndent = "";
    if (!key.isEmpty()) {
      newIndent = growIndent(indentationPolicy);
    }

    String fullKey = key.isEmpty() ? newKey : key + "." + newKey;

    WriterContext subContext = new WriterContext(fullKey, newIndent, output, indentationPolicy, timeZone);
    if (!empty) {
      subContext.empty = false;
    }
    
    return subContext;
  }

  WriterContext pushTableFromArray() {
    WriterContext subContext = new WriterContext(key, currentTableIndent, output, indentationPolicy, timeZone);
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
    for (int i = 0; i < indentationPolicy.getArrayDelimiterPadding(); i++) {
      write(' ');
    }
  }

  void indent() {
    if (!key.isEmpty()) {
      write(currentFieldIndent);
    }
  }
  
  DateFormat getDateFormat() {
    String format = "yyyy-MM-dd'T'HH:m:ss'Z'";
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    formatter.setTimeZone(timeZone);
    
    return formatter;
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

  private WriterContext(String key, String tableIndent, Writer output, WriterIndentationPolicy indentationPolicy, TimeZone timeZone) {
    this.key = key;
    this.output = output;
    this.indentationPolicy = indentationPolicy;
    this.currentTableIndent = tableIndent;
    this.currentFieldIndent = tableIndent + fillStringWithSpaces(this.indentationPolicy.getKeyValueIndent());
    this.timeZone = timeZone;
  }

  public WriterContext setArrayKey(String arrayKey) {
    this.arrayKey = arrayKey;
    return this;
  }

  public String getContextPath() {
    return key.isEmpty() ? arrayKey : key + "." + arrayKey;
  }
}

package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

import java.util.regex.Pattern;

class TomlParser {
  private static final String STRING_LITERAL_DELIMITER = "'''";
  private static final Pattern MULTILINE_ARRAY_REGEX = Pattern.compile("\\s*\\[([^\\]]*)");
  private static final Pattern MULTILINE_ARRAY_REGEX_END = Pattern.compile("\\s*\\]");
  private static final ValueConverters VALUE_ANALYSIS = new ValueConverters();

  private final Results results = new Results();

  Results run(String tomlString) {
    if (tomlString.isEmpty()) {
      return results;
    }

    String[] lines = tomlString.split("[\\n\\r]");
    StringBuilder multilineBuilder = new StringBuilder();
    Multiline multiline = Multiline.NONE;
    
    String key = null;
    String value = null;

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      if (line != null && multiline.isTrimmable()) {
        line = line.trim();
      }

      if (isComment(line) || line.isEmpty()) {
        continue;
      }

      if (isTableArray(line)) {
        String tableName = getTableArrayName(line);
        if (tableName != null) {
          results.startTableArray(tableName);
          String afterTableName = line.substring(tableName.length() + 4);
          if (!isComment(afterTableName)) {
            results.errors.append("Invalid table array definition: " + line + "\n\n");
          }
        } else {
          results.errors.append("Invalid table array definition: " + line + "\n\n");
        }

        continue;
      }

      if (multiline.isNotMultiline() && isTable(line)) {
        String tableName = getTableName(line);
        if (tableName != null) {
          results.startTables(tableName);
        } else {
          results.errors.append("Invalid table definition: " + line + "\n\n");
        }

        continue;
      }
      
      if (multiline.isNotMultiline() && !line.contains("=")) {
        results.errors.append("Invalid key definition: " + line);
        continue;
      }

      String[] pair = line.split("=", 2);

      if (multiline.isNotMultiline() && MULTILINE_ARRAY_REGEX.matcher(pair[1].trim()).matches()) {
        multiline = Multiline.ARRAY;
        key = pair[0].trim();
        multilineBuilder.append(removeComment(pair[1]));
        continue;
      }

      if (multiline.isNotMultiline() && pair[1].trim().startsWith("\"\"\"")) {
        multiline = Multiline.STRING;
        multilineBuilder.append(pair[1]);
        key = pair[0].trim();

        if (pair[1].trim().indexOf("\"\"\"", 3) > -1) {
          multiline = Multiline.NONE;
          pair[1] = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          if (multilineBuilder.toString().trim().length() > 3) {
            multilineBuilder.append('\n');
          }
          continue;
        }
      }
      
      if (multiline.isNotMultiline() && pair[1].trim().startsWith(STRING_LITERAL_DELIMITER)) {
        multiline = Multiline.STRING_LITERAL;
        multilineBuilder.append(pair[1]);
        key = pair[0].trim();

        if (pair[1].trim().indexOf(STRING_LITERAL_DELIMITER, 3) > -1) {
          multiline = Multiline.NONE;
          pair[1] = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          if (multilineBuilder.toString().trim().length() > 3) {
            multilineBuilder.append('\n');
          }
          continue;
        }
      }
      
      if (multiline == Multiline.ARRAY) {
        String lineWithoutComment = removeComment(line);
        multilineBuilder.append(lineWithoutComment);
        if (MULTILINE_ARRAY_REGEX_END.matcher(lineWithoutComment).matches()) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          continue;
        }
      } else if (multiline == Multiline.STRING) {
        multilineBuilder.append(line);
        if (line.contains("\"\"\"")) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          multilineBuilder.append('\n');
          continue;
        }
      } else if (multiline == Multiline.STRING_LITERAL) {
        multilineBuilder.append(line);
        if (line.contains(STRING_LITERAL_DELIMITER)) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          multilineBuilder.append('\n');
          continue;
        }
      } else {
        key = pair[0].trim();
        value = pair[1].trim();
      }

      if (!isKeyValid(key)) {
        results.errors.append("Invalid key name: " + key + "\n");
        continue;
      }

      Object convertedValue = VALUE_ANALYSIS.convert(value);

      if (convertedValue != INVALID) {
        results.addValue(key, convertedValue);
      } else {
        results.errors.append("Invalid key/value: " + key + " = " + value + "\n");
      }
    }
    
    if (multiline != Multiline.NONE) {
      results.errors.append("Unterminated multiline " + multiline.toString().toLowerCase().replace('_', ' ') + "\n");
    }

    return results;
  }

  private boolean isTableArray(String line) {
    return line.startsWith("[[");
  }
  
  private String getTableArrayName(String line) {
    return Keys.getTableArrayName(line);
  }

  private boolean isTable(String line) {
    return line.startsWith("[");
  }

  private String getTableName(String line) {
    return Keys.getTableName(line);
  }

  private boolean isKeyValid(String key) {
    if (key.contains("#") || key.trim().isEmpty()) {
      return false;
    }

    return true;
  }

  private boolean isComment(String line) {
    if (line == null || line.isEmpty()) {
      return true;
    }

    char[] chars = line.toCharArray();

    for (char c : chars) {
      if (Character.isWhitespace(c)) {
        continue;
      }

      return c == '#';
    }

    return false;
  }

  private String removeComment(String line) {
    line = line.trim();
    if (line.startsWith("\"")) {
      int startOfComment = line.indexOf('#', line.lastIndexOf('"'));
      if (startOfComment > -1) {
        return line.substring(0, startOfComment - 1).trim();
      }
    } else {
      int startOfComment = line.indexOf('#');
      if (startOfComment > -1) {
        return line.substring(0, startOfComment - 1).trim();
      }
    }

    return line;
  }
  
  private static enum Multiline {
    NONE, ARRAY, STRING, STRING_LITERAL;
    
    public boolean isNotMultiline() {
      return this == NONE;
    }
    
    public boolean isTrimmable() {
      return this == NONE || this == ARRAY;
    }
  }
}

package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueAnalysis.INVALID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser {
  private static final Pattern TABLE_REGEX = Pattern.compile("\\s?\\[(.*)\\](.*)");
  private static final Pattern TABLE_ARRAY_REGEX = Pattern.compile("\\s?\\[\\[(.*)\\]\\](.*)");
  private static final Pattern MULTILINE_ARRAY_REGEX = Pattern.compile("\\s*\\[([^\\]]*)");
  private static final Pattern MULTILINE_ARRAY_REGEX_END = Pattern.compile("\\s*\\]");

  public static void main(String[] args) {
    System.out.println(MULTILINE_ARRAY_REGEX.matcher("  [ ]").matches());
  }

  private final Results results = new Results();

  public Results run(String tomlString) {
    if (tomlString.isEmpty()) {
      return results;
    }

    String[] lines = tomlString.split("[\\n\\r]");
    StringBuilder multilineBuilder = new StringBuilder();
    boolean multiline = false;

    String key = null;
    String value = null;

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      if (line != null) {
        line = line.trim();
      }

      if (isComment(line) || line.isEmpty()) {
        continue;
      }

      if (isTableArray(line)) {
        Matcher matcher = TABLE_ARRAY_REGEX.matcher(line);
        matcher.matches();
        String tableName = matcher.group(1);
        results.startTableArray(tableName);

        String afterTableName = matcher.group(2);
        if (!isComment(afterTableName)) {
          results.errors.append("Invalid table array definition: " + line + "\n\n");
        }

        continue;
      }

      if (isTable(line)) {
        Matcher matcher = TABLE_REGEX.matcher(line);
        matcher.matches();
        String tableName = matcher.group(1);
        results.startTables(tableName);
        String afterTableName = matcher.group(2);
        if (!isComment(afterTableName)) {
          results.errors.append("Invalid table definition: " + line + "\n\n");
        }

        continue;
      }

      String[] pair = line.split("=");

      if (!multiline && MULTILINE_ARRAY_REGEX.matcher(pair[1].trim()).matches()) {
        multiline = true;
        key = pair[0].trim();
        multilineBuilder.append(pair[1].trim());
        continue;
      }


      if (multiline) {
        multilineBuilder.append(line);
        if (MULTILINE_ARRAY_REGEX_END.matcher(line).matches()) {
          multiline = false;
          value = multilineBuilder.toString();
          multilineBuilder.delete(0, multilineBuilder.length() - 1);
        } else {
          continue;
        }
      } else {
        key = pair[0].trim();
        value = pair[1].trim();
      }

      if (!isKeyValid(key)) {
        results.errors.append("Invalid key name: " + key);
        continue;
      }

      ValueAnalysis lineAnalysis = new ValueAnalysis(value.trim());

      Object convertedValue = lineAnalysis.getValue();

      if (convertedValue != INVALID) {
        results.addValue(key, convertedValue);
      } else {
        results.errors.append("Invalid key/value: " + key + " = " + value);
      }
    }

    return results;
  }

  private boolean isTableArray(String line) {
    return TABLE_ARRAY_REGEX.matcher(line).matches();
  }

  private boolean isTable(String line) {
    return TABLE_REGEX.matcher(line).matches();
  }

  private boolean isKeyValid(String key) {
    if (key.contains(".")) {
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
}

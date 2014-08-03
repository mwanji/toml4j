package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;

@BuildParseTree
class TomlParser extends BaseParser<Object> {

  static class Results {
    public Map<String, Object> values = new HashMap<String, Object>();
    public Set<String> tables = new HashSet<String>();
    public StringBuilder errors = new StringBuilder();
  }

  public Rule Toml() {
    return Sequence(push(new TomlParser.Results()), push(((TomlParser.Results) peek()).values), OneOrMore(FirstOf(TableArray(), Table(), '\n', Comment(), Key())));
  }

  Rule Table() {
    return Sequence(Sequence(TableDelimiter(), TableName(), addTable((String) pop()), TableDelimiter(), Spacing()), checkTable(match()));
  }

  Rule TableArray() {
    return Sequence(Sequence(TableDelimiter(), TableDelimiter(), TableName(), addTableArray((String) pop()), TableDelimiter(), TableDelimiter(), Spacing()), checkTable(match()));
  }

  boolean checkTable(String definition) {
    String afterBracket = definition.substring(definition.lastIndexOf(']') + 1);
    for (char character : afterBracket.toCharArray()) {
      if (character == '#') {
        return true;
      }

      if (!Character.isWhitespace(character)) {
        results().errors.append("Invalid table definition: ").append(definition).append(". You may have forgotten a #.\n");
      }
    }
    return true;
  }

  Rule Key() {
    return Sequence(Spacing(), KeyName(), EqualsSign(), VariableValues(), Spacing(), swap(), addKey((String) pop(), pop()));
  }

  Rule TableName() {
    return Sequence(OneOrMore(TestNot(TableDelimiter()), FirstOf(Letter(), Digit(), ANY)), push(match()));
  }

  Rule KeyName() {
    return Sequence(OneOrMore(TestNot(EqualsSign()), ANY), push(match()));
  }

  Rule VariableValues() {
    return FirstOf(ArrayValue(), DateValue(), BooleanValue(), NumberValue(), StringValue());
  }

  Rule ArrayValue() {
    return Sequence(push(ArrayList.class), '[', Spacing(), ZeroOrMore(VariableValues(), Optional(ArrayDelimiter())), Spacing(), ']', pushList());
  }

  Rule DateValue() {
    return Sequence(Sequence(Year(), '-', Month(), '-', Day(), 'T', Digit(), Digit(), ':', Digit(), Digit(), ':', Digit(), Digit(), 'Z'), pushDate(match()));
  }

  Rule BooleanValue() {
    return Sequence(FirstOf("true", "false"), push(Boolean.valueOf(match())));
  }

  Rule NumberValue() {
    return Sequence(Sequence(Optional('-'), OneOrMore(FirstOf(Digit(), '.'))), pushNumber(match()));
  }

  Rule StringValue() {
    return Sequence(push(new StringBuilder()), '"', OneOrMore(TestNot('"'), FirstOf(UnicodeCharacter(), SpecialCharacter(), AnyCharacter())), pushString(((StringBuilder) pop()).toString()), '"');
  }

  Rule Year() {
    return Sequence(Digit(), Digit(), Digit(), Digit());
  }

  Rule Month() {
    return Sequence(CharRange('0', '1'), Digit());
  }

  Rule Day() {
    return Sequence(CharRange('0', '3'), Digit());
  }

  Rule Digit() {
    return CharRange('0', '9');
  }

  Rule Letter() {
    return CharRange('a', 'z');
  }

  Rule UnicodeCharacter() {
    return Sequence(Sequence('\\', 'u', OneOrMore(FirstOf(CharRange('0', '9'), CharRange('A', 'F')))), pushCharacter(match()));
  }

  Rule SpecialCharacter() {
    return Sequence(Sequence('\\', ANY), pushCharacter(match()));
  }

  Rule AnyCharacter() {
    return Sequence(TestNot(NewLine()), ANY, pushCharacter(match()));
  }

  @SuppressNode
  Rule TableDelimiter() {
    return AnyOf("[]");
  }

  @SuppressNode
  Rule EqualsSign() {
    return Sequence(Spacing(), '=', Spacing());
  }

  @SuppressNode
  Rule Spacing() {
    return ZeroOrMore(FirstOf(Comment(), Whitespace(), NewLine(), AnyOf("\f")));
  }

  Rule IllegalCharacters() {
    return Sequence(ZeroOrMore(Whitespace()), OneOrMore(TestNot('#', NewLine()), ANY));
  }

  @SuppressNode
  Rule Whitespace() {
    return AnyOf(" \t");
  }

  @SuppressNode
  Rule NewLine() {
    return AnyOf("\r\n");
  }

  @SuppressNode
  Rule ArrayDelimiter() {
    return Sequence(Spacing(), ',', Spacing());
  }

  @SuppressNode
  Rule Comment() {
    return Sequence(ZeroOrMore(Whitespace()), '#', ZeroOrMore(TestNot(NewLine()), ANY), FirstOf(NewLine(), EOI));
  }

  boolean addTableArray(String name) {
    return addTable(name, true);
  }

  boolean addTable(String name) {
    return addTable(name, false);
  }

  boolean addKey(String key, Object value) {
    if (key.contains(".")) {
      results().errors.append(key).append(" is invalid: key names may not contain a dot!\n");

      return true;
    }
    putValue(key, value);

    return true;
  }

  boolean pushList() {
    ArrayList<Object> list = new ArrayList<Object>();
    while (peek() != ArrayList.class) {
      Object listItem = pop();
      if (list.isEmpty() || list.get(0).getClass().isAssignableFrom(listItem.getClass()) || listItem.getClass().isAssignableFrom(list.get(0).getClass())) {
        list.add(0, listItem);
      } else {
        results().errors.append("Attempt to create mixed array!");
      }
    }

    poke(list);
    return true;
  }

  boolean pushDate(String dateString) {
    String s = dateString.replace("Z", "+00:00");
    try {
      s = s.substring(0, 22) + s.substring(23);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
      dateFormat.setLenient(false);
      Date date = dateFormat.parse(s);
      push(date);
      return true;
    } catch (Exception e) {
      results().errors.append("Invalid date: ").append(dateString).append("\n");
      return false;
    }
  }

  boolean pushNumber(String number) {
    if (number.contains(".")) {
      push(Double.valueOf(number));
    } else {
      push(Long.valueOf(number));
    }
    return true;
  }

  boolean pushString(String line) {
    StringBuilder builder = new StringBuilder();

    String[] split = line.split("\\\\n");
    for (String string : split) {
      builder.append(string).append('\n');
    }
    builder.deleteCharAt(builder.length() - 1);
    push(builder.toString());

    return true;
  }

  boolean pushCharacter(String sc) {
    StringBuilder sb = (StringBuilder) peek();
    if (sc.equals("\\n")) {
      sb.append('\n');
    } else if (sc.equals("\\\"")) {
      sb.append('\"');
    } else if (sc.equals("\\t")) {
      sb.append('\t');
    } else if (sc.equals("\\r")) {
      sb.append('\r');
    } else if (sc.equals("\\\\")) {
      sb.append('\\');
    } else if (sc.equals("\\/")) {
      sb.append('/');
    } else if (sc.equals("\\b")) {
      sb.append('\b');
    } else if (sc.equals("\\f")) {
      sb.append('\f');
    } else if (sc.startsWith("\\u")) {
      sb.append(Character.toChars(Integer.parseInt(sc.substring(2), 16)));
    } else if (sc.startsWith("\\")) {
      results().errors.append(sc + " is a reserved special character and cannot be used!\n");
    } else {
      sb.append(sc);
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  private boolean addTable(String name, boolean array) {
    String[] split = name.split("\\.");

    while (getContext().getValueStack().size() > 2) {
      drop();
    }

    Map<String, Object> newTable = (Map<String, Object>) getContext().getValueStack().peek();

    boolean addedToTables = results().tables.add(name);
    if (!addedToTables && !array) {
      results().errors.append("Could not create table ").append(name).append(": table already exists!\n");

      return true;
    }

    for (int i = 0; i < split.length; i++) {
      String splitKey = split[i];

      if (!newTable.containsKey(splitKey)) {
        if (array && isLast(split, i)) {
          ArrayList<Map<String, Object>> newTableList = new ArrayList<Map<String, Object>>();
          newTable.put(splitKey, newTableList);
        } else {
          newTable.put(splitKey, new HashMap<String, Object>());
        }
      }
      Object currentValue = newTable.get(splitKey);
      if (!(currentValue instanceof List) && !(currentValue instanceof Map)) {
        results().errors.append("Could not create table ").append(name).append(": key already has a value!\n");

        return true;
      }

      if (currentValue instanceof List) {
        List<Map<String, Object>> currentList = (List<Map<String, Object>>) currentValue;
        if (array && isLast(split, i)) {
          Map<String, Object> newTableListItem = new HashMap<String,Object>();
          currentList.add(newTableListItem);
          currentValue = newTableListItem;
        } else {
          currentValue = currentList.get(currentList.size() - 1);
        }
      }

      newTable = (Map<String, Object>) currentValue;
    }

    push(newTable);

    return true;
  }

  @SuppressWarnings("unchecked")
  private void putValue(String name, Object value) {
    Map<String, Object> values = (Map<String, Object>) peek();
    Object top = peek();
    if (top instanceof List) {
      values = ((List<Map<String, Object>>) top).get(((List<Map<String, Object>>) top).size() - 1);
    }
    if (values.containsKey(name)) {
      results().errors.append("Key ").append(name).append(" already exists!\n");
      return;
    }
    values.put(name, value);
  }

  private boolean isLast(String[] array, int index) {
    return index == array.length - 1;
  }

  private TomlParser.Results results() {
    return (Results) peek(getContext().getValueStack().size() - 1);
  }
}

package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
    public Set<String> keyGroups = new HashSet<String>();
    public StringBuilder errors = new StringBuilder();
  }

  public Rule Toml() {
    return Sequence(push(new TomlParser.Results()), push(((TomlParser.Results) peek()).values), OneOrMore(FirstOf(KeyGroup(), '\n', Comment(), Key())));
  }

  Rule KeyGroup() {
    return Sequence(Sequence(KeyGroupDelimiter(), KeyGroupName(), addKeyGroup((String) pop()), KeyGroupDelimiter(), Spacing()), checkKeyGroup(match()));
  }

  Rule IllegalCharacters() {
    return ZeroOrMore(TestNot(NewLine()), ANY);
  }

  boolean checkKeyGroup(String definition) {
    String afterBracket = definition.substring(definition.indexOf(']') + 1);
    for (char character : afterBracket.toCharArray()) {
      if (character == '#') {
        return true;
      }

      if (!Character.isWhitespace(character)) {
        results().errors.append("Invalid key group definition: ").append(definition).append(". You may have forgotten a #");
      }
    }
    return true;
  }

  Rule Key() {
    return Sequence(Spacing(), KeyName(), EqualsSign(), VariableValues(), Spacing(), swap(), addKey((String) pop(), pop()));
  }

  Rule KeyGroupName() {
    return Sequence(OneOrMore(TestNot(KeyGroupDelimiter()), FirstOf(Letter(), Digit(), ANY)), push(match()));
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
    return Sequence(OneOrMore(FirstOf(Digit(), '.')), pushNumber(match()));
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
    return Sequence(ANY, pushCharacter(match()));
  }

  @SuppressNode
  Rule KeyGroupDelimiter() {
    return AnyOf("[]");
  }

  @SuppressNode
  Rule EqualsSign() {
    return Sequence(Spacing(), '=', Spacing());
  }

  @SuppressNode
  Rule Spacing() {
    return ZeroOrMore(FirstOf(Comment(), AnyOf(" \t\r\n\f")));
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
    return Sequence('#', ZeroOrMore(TestNot(AnyOf("\r\n")), ANY), FirstOf("\r\n", '\r', '\n', EOI));
  }

  @SuppressWarnings("unchecked")
  boolean addKeyGroup(String name) {
    String[] split = name.split("\\.");
    name = split[split.length - 1];

    while (getContext().getValueStack().size() > 2) {
      drop();
    }

    Map<String, Object> newKeyGroup = (Map<String, Object>) getContext().getValueStack().peek();

    if (!results().keyGroups.add(name)) {
      results().errors.append("Could not create key group ").append(name).append(": key group already exists!\n");

      return true;
    }

    for (String splitKey : split) {
      if (!newKeyGroup.containsKey(splitKey)) {
        newKeyGroup.put(splitKey, new HashMap<String, Object>());
      }
      Object currentValue = newKeyGroup.get(splitKey);
      if (!(currentValue instanceof Map)) {
        results().errors.append("Could not create key group ").append(name).append(": key already has a value!\n");

        return true;
      }
      newKeyGroup = (Map<String, Object>) currentValue;
    }

    push(newKeyGroup);

    return true;
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
      list.add(0, pop());
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
  void putValue(String name, Object value) {
    Map<String, Object> values = (Map<String, Object>) peek();
    if (values.containsKey(name)) {
      results().errors.append("Key ").append(name).append(" already exists!\n");
      return;
    }
    values.put(name, value);
  }

  TomlParser.Results results() {
    return (Results) peek(getContext().getValueStack().size() - 1);
  }
}

package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;

@BuildParseTree
class TomlParser extends BaseParser<Object> {

  static class Results {
    public Map<String, Object> values = new HashMap<String, Object>();
    public StringBuilder errors = new StringBuilder();
  }

  public Rule Toml() {
    return Sequence(push(new TomlParser.Results()), push(((TomlParser.Results) peek()).values), OneOrMore(FirstOf(KeyGroup(), Comment(), Key())));
  }

  Rule KeyGroup() {
    return Sequence(KeyGroupDelimiter(), KeyGroupName(), addKeyGroup((String) pop()), KeyGroupDelimiter(), Spacing());
  }

  Rule Key() {
    return Sequence(Spacing(), KeyName(), EqualsSign(), VariableValues(), Spacing(), swap(), addKey((String) pop(), pop()));
  }

  Rule KeyGroupName() {
    return Sequence(OneOrMore(FirstOf(Letter(), Digit(), '.', '_')), push(match()));
  }

  Rule KeyName() {
    return Sequence(OneOrMore(FirstOf(Letter(), Digit(), '_', '.')), push(match()));
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
    return Sequence('"', OneOrMore(TestNot('"'), ANY), pushString(match()), '"');
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
    for (String splitKey : split) {
      if (!newKeyGroup.containsKey(splitKey)) {
        newKeyGroup.put(splitKey, new HashMap<String, Object>());
      }
      Object currentValue = newKeyGroup.get(splitKey);
      if (!(currentValue instanceof Map)) {
        results().errors.append("Could not create key group ").append(name).append(": key already exists!");

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
      results().errors.append("Invalid date: ").append(dateString);
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

  @SuppressWarnings("unchecked")
  void putValue(String name, Object value) {
    Map<String, Object> values = (Map<String, Object>) peek();
    if (values.containsKey(name)) {
      results().errors.append("Key ").append(name).append(" already exists!");
      return;
    }
    values.put(name, value);
  }

  TomlParser.Results results() {
    return (Results) peek(getContext().getValueStack().size() - 1);
  }
}

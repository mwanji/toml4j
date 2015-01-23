package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree
class ValueParser extends BaseParser<List<Object>> {
  
  public Rule Array() {
    return FirstOf(EmptyArray(), Sequence('[', startList(), OneOrMore(FirstOf(NonEmptyArray(), ' ', ',')), ']', endList()));
  }
  
  Rule NonEmptyArray() {
    return FirstOf(Array(), OneOrMore(TestNot(']'), FirstOf(StringToken(), Array(), ',', ' ', OtherValue())));
  }
  
  Rule StringToken() {
    return Sequence(Sequence('"', ZeroOrMore(Sequence(TestNot('"'), ANY)), '"'), pushToken(match()));
  }
  
  Rule EmptyArray() {
    return Sequence('[', ']', startList(), endList());
  }
  
  Rule OtherValue() {
    return Sequence(ZeroOrMore(NoneOf("],")), pushToken(match()));
  }
  
  boolean startList() {
    ArrayList<Object> newTokens = new ArrayList<Object>();

    if (!getContext().getValueStack().isEmpty()) {
      peek().add(newTokens);
    }
    push(newTokens);

    return true;
  }

  boolean endList() {
    if (getContext().getValueStack().size() > 1) {
      pop();
    }

    return true;
  }

  boolean pushToken(String s) {
    peek().add(s);

    return true;
  }
}
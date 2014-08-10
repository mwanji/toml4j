package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Rule;

public class ParboiledParser extends BaseParser<List<Object>> {

  public Rule Array() {
    return Sequence('[', startList(), ArrayDo(), ']', endList());
  }

  Rule ArrayDo() {
    return OneOrMore(TestNot(']'), FirstOf(String(), Array(), ',', ' ', OtherValue()));
  }

  Rule String() {
    return Sequence(Sequence('"', ZeroOrMore(TestNot('"'), ANY), '"'), pushToken(match()));
  }

  Rule OtherValue() {
    return Sequence(ZeroOrMore(NoneOf("],")), pushToken(match()));
  }

//  private Rule Comment() {
//    return Sequence('#' , ZeroOrMore(TestNot(EOI), ANY));
//  }

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
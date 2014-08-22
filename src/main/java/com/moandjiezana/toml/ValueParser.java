package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

@BuildParseTree
class ValueParser extends BaseParser<List<Object>> {
  
  public static void main(String[] args) {
    ParsingResult<Object> parsingResult = new RecoveringParseRunner<Object>(ValueConverterUtils.parser().T()).run("'''abc''' # comment");
    
    System.out.println(ParseTreeUtils.printNodeTree(parsingResult));
  }
  
  public Rule T() {
    return Sequence("'''", OneOrMore(TestNot("'''"), ANY), "'''", Comment());
  }

  public Rule Array() {
    return FirstOf(EmptyArray(), Sequence('[', startList(), OneOrMore(FirstOf(NonEmptyArray(), ' ', ',')), ']', endList()));
  }

  public Rule Table() {
    return Sequence('[', startList(), Sequence(OneOrMore(NoneOf("[]")), pushToken(match())), ']', endList(), Comment());
  }
  
  public Rule TableArray() {
    return Sequence('[', '[', startList(), Sequence(OneOrMore(NoneOf("[]")), pushToken(match())), ']', ']', endList(), FirstOf(EOI, Sequence(TestNot(']'), ANY)));
  }

  public Rule LiteralString() {
    return FirstOf(EmptyLiteralString(), Sequence('\'', OneOrMore(TestNot("'"), ANY), startList(), pushToken(match()) , '\'', endList(), Comment()));
  }
  
  public Rule MultilineLiteralString() {
    return FirstOf(EmptyMultilineLiteralString(), Sequence("'''", startList(), Sequence(OneOrMore(TestNot("'''"), ANY), pushToken(match())), "'''", endList(), Comment()));
  }
  
  public Rule Boolean() {
    return Sequence(startList(), FirstOf("true", "false"), pushToken(match()), endList(), Comment());
  }
  
  public Rule Integer() {
    return Sequence(startList(), Sequence(Sequence(Optional('-'), OneOrMore(CharRange('0', '9'))), pushToken(match())), endList(), Comment());
  }

  Rule NonEmptyArray() {
    return FirstOf(Array(), OneOrMore(TestNot(']'), FirstOf(StringToken(), Array(), ',', ' ', OtherValue())));
  }
  
  Rule StringToken() {
    return Sequence(Sequence('"', ZeroOrMore(Sequence(TestNot('"'), ANY)), '"'), pushToken(match()));
  }
  
  Rule EmptyLiteralString() {
    return Sequence('\'', '\'', startList(), pushToken(""), endList());
  }
  
  Rule EmptyMultilineLiteralString() {
    return Sequence("'''", "'''", startList(), pushToken(""), endList(), Comment());
  }
  
  Rule EmptyArray() {
    return Sequence('[', ']', startList(), endList());
  }

  Rule OtherValue() {
    return Sequence(ZeroOrMore(NoneOf("],")), pushToken(match()));
  }
  
  Rule Comment() {
    return FirstOf(EOI, OneOrMore(' ', Sequence('#', ZeroOrMore(ANY))));
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
package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

@BuildParseTree
public class ParboiledParser extends BaseParser<List<Object>> {

  public static void main(String[] args) {
    ParboiledParser parser = Parboiled.createParser(ParboiledParser.class);

    ParsingResult<List<Object>> parsingResult = new RecoveringParseRunner<List<Object>>(parser.Array()).run("[ [], []]");
    System.out.println(ParseTreeUtils.printNodeTree(parsingResult));
    
    System.out.println(parsingResult.resultValue);

  }

  public Rule Array() {
    return FirstOf(EmptyArray(), Sequence('[', startList(), OneOrMore(FirstOf(NonEmptyArray(), ' ', ',')), ']', endList()));
  }

  public Rule Table() {
    return Sequence('[', startList(), Sequence(OneOrMore(NoneOf("[]")), pushToken(match())), ']', endList(), FirstOf(EOI, Sequence(TestNot(']'), ANY)));
  }
  
  public Rule TableArray() {
    return Sequence('[', '[', startList(), Sequence(OneOrMore(NoneOf("[]")), pushToken(match())), ']', ']', endList(), FirstOf(EOI, Sequence(TestNot(']'), ANY)));
  }

  Rule NonEmptyArray() {
    return FirstOf(Array(), OneOrMore(TestNot(']'), FirstOf(String(), Array(), ',', ' ', OtherValue())));
  }
  
  Rule EmptyArray() {
    return Sequence('[', ']', startList(), endList());
  }

  Rule String() {
    return Sequence(Sequence('"', ZeroOrMore(TestNot('"'), ANY), '"'), pushToken(match()));
  }

  Rule OtherValue() {
    return Sequence(ZeroOrMore(NoneOf("],")), pushToken(match()));
  }

  @SuppressNode
  Rule Comment() {
    return OneOrMore(FirstOf(AnyOf("\t"), EOI));
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
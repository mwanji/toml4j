package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import static com.moandjiezana.toml.ValueConverterUtils.parse;
import static com.moandjiezana.toml.ValueConverterUtils.parser;

import java.util.List;

import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

class MultilineLiteralStringConverter implements ValueConverter {
  
  public static void main(String[] args) {
    ParsingResult<List<java.lang.String>> parsingResult = new RecoveringParseRunner<List<String>>(ValueConverterUtils.parser().MultilineLiteralString()).run("'''abc''' # comment");
    
    if (parsingResult.hasErrors()) {
      for (ParseError parseError : parsingResult.parseErrors) {
        System.out.println(parseError.getInputBuffer().extract(0, 1000));
      }
    }
    
    System.out.println(ParseTreeUtils.printNodeTree(parsingResult));
  }

  static final MultilineLiteralStringConverter MULTILINE_LITERAL_STRING_CONVERTER = new MultilineLiteralStringConverter(); 
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("'''");
  }

  @Override
  public Object convert(String s) {
    List<String> result = parse(parser().MultilineLiteralString(), s);
    
    if (result == null) {
      return INVALID;
    }
    
    return result.get(0);
  }

  private MultilineLiteralStringConverter() {}
}

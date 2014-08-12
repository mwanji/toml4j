package com.moandjiezana.toml.values;

import static com.moandjiezana.toml.values.ValueAnalysis.INVALID;

import java.util.ArrayList;
import java.util.List;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParsingResult;

import com.moandjiezana.toml.StatementParser;

class ArrayParser implements ValueParser {

  static final ArrayParser ARRAY_PARSER = new ArrayParser();

  private static final List<Object> INVALID_ARRAY = new ArrayList<Object>();
  private static final ValueAnalysis VALUE_ANALYSIS = new ValueAnalysis();

  @Override
  public boolean canParse(String s) {
    return s.startsWith("[");
  }

  @Override
  public Object parse(String s) {
    StatementParser parser = Parboiled.createParser(StatementParser.class);
    ParsingResult<List<Object>> parsingResult = new BasicParseRunner<List<Object>>(parser.Array()).run(s);
    List<Object> tokens = parsingResult.resultValue;
    List<Object> values = convertList(tokens);

    if (values == INVALID_ARRAY) {
      return INVALID;
    }

    return values;
  }

  private List<Object> convertList(List<Object> tokens) {
    ArrayList<Object> nestedList = new ArrayList<Object>();

    for (Object token : tokens) {
      if (token instanceof String) {
        Object converted = VALUE_ANALYSIS.convert(((String) token).trim());
        if (converted == INVALID) {
          return INVALID_ARRAY;
        }
        if (isHomogenousArray(converted, nestedList)) {
          nestedList.add(converted);
        } else {
          return INVALID_ARRAY;
        }
      } else if (token instanceof List) {
        @SuppressWarnings("unchecked")
        List<Object> convertedList = convertList((List<Object>) token);
        if (convertedList != INVALID_ARRAY && isHomogenousArray(convertedList, nestedList)) {
          nestedList.add(convertedList);
        } else {
          return INVALID_ARRAY;
        }
      }
    }

    return nestedList;
  }

  private boolean isHomogenousArray(Object o, List<?> values) {
    return values.isEmpty() || values.get(0).getClass().isAssignableFrom(o.getClass()) || o.getClass().isAssignableFrom(values.get(0).getClass());
  }

  private ArrayParser() {}
}

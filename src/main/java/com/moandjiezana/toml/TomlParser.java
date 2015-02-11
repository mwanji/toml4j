package com.moandjiezana.toml;

import static com.moandjiezana.toml.IdentifierConverter.IDENTIFIER_CONVERTER;
import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

import java.util.concurrent.atomic.AtomicInteger;

import com.moandjiezana.toml.ValueConverterUtils.Unterminated;

class TomlParser {

  Results run(String tomlString) {
    final Results results = new Results();
    
    if (tomlString.isEmpty()) {
      return results;
    }
    
    char[] chars = tomlString.toCharArray();
    AtomicInteger index = new AtomicInteger();
    boolean inComment = false;
    AtomicInteger line = new AtomicInteger(1);
    Identifier identifier = null;
    Object value = null;
    
    for (int i = index.get(); i < chars.length; i = index.incrementAndGet()) {
      char c = chars[i];
      
      if (c == '#' && !inComment) {
        inComment = true;
      } else if (!Character.isWhitespace(c) && !inComment && identifier == null) {
        Identifier id = IDENTIFIER_CONVERTER.convert(chars, index);
        
        if (id.isValid()) {
          char next = chars[index.get()];
          if (index.get() < chars.length -1 && !id.acceptsNext(next)) {
            results.errors.invalidTextAfterIdentifier(id, next, line.get());
          } else if (id.isKey()) {
            identifier = id;
          } else if (id.isTable()) {
            results.startTables(Keys.getTableName(id.getName()));
          } else if (id.isTableArray()) {
            results.startTableArray(Keys.getTableArrayName(id.getName()));
          }
          inComment = next == '#';
        } else {
          results.errors.invalidIdentifier(id, line.get());
        }
      } else if (c == '\n') {
        inComment = false;
        identifier = null;
        value = null;
        line.incrementAndGet();
      } else if (!inComment && identifier != null && identifier.isKey() && value == null && !Character.isWhitespace(c)) {
        int startIndex = index.get();
        Object converted = ValueConverters.CONVERTERS.convert(tomlString, index);
        value = converted;
        
        if (converted == INVALID) {
          results.errors.invalidValue(identifier.getName(), tomlString.substring(startIndex, Math.min(index.get(), tomlString.length() - 1)), line.get());
        } else if (converted instanceof Unterminated) {
          results.errors.unterminated(identifier.getName(), ((Unterminated) converted).payload, line.get());
        } else {
          results.addValue(identifier.getName(), converted);
        }
      } else if (value != null && !inComment && !Character.isWhitespace(c)) {
        results.errors.invalidTextAfterIdentifier(identifier, c, line.get());
      }
    }

    return results;
  }
}

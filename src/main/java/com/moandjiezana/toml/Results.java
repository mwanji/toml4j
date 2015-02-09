package com.moandjiezana.toml;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Results {
  
  static class Errors {
    
    private final StringBuilder sb = new StringBuilder();
    
    void duplicateTable(String table, int line) {
      sb.append("Duplicate table definition: [")
        .append(table)
        .append("]\n");
    }
    
    void emptyImplicitTable(String table, int line) {
      sb.append("Invalid table definition due to empty implicit table name: ");
      if (!table.startsWith("[")) {
        sb.append('[');
      }
      sb.append(table);
      if (!table.endsWith("]")) {
        sb.append(']');
      }
      sb.append("\n");
    }
    
    void invalidTable(String table, int line) {
      sb.append("Invalid table definition on line ")
        .append(line)
        .append(": ");
      if (!table.startsWith("[")) {
        sb.append('[');
      }
      sb.append(table);
      if (!table.endsWith("]")) {
        sb.append(']');
      }
      sb.append("]\n");
    }
    
    void duplicateKey(String key, int line) {
      sb.append("Duplicate key: ")
        .append(key)
        .append('\n');
    }
    
    void invalidKey(String key, int line) {
      sb.append("Invalid key");
      if (line > -1) {
        sb.append(" on line ")
          .append(line);
      }
      sb.append(": ")
        .append(key)
        .append('\n');
    }
    
    void invalidTableArray(String tableArray, int line) {
      sb.append("Invalid table array definition on line ")
        .append(line)
        .append(": ")
        .append(tableArray)
        .append('\n');
    }
    
    void invalidValue(String key, String value, int line) {
      sb.append("Invalid value on line ")
        .append(line)
        .append(": ")
        .append(key)
        .append(" = ")
        .append(value)
        .append('\n');
    }
    
    void unterminated(String key, String multiline, int line) {
      sb.append("Unterminated multiline value on line ")
        .append(line)
        .append(": ")
        .append(key)
        .append(" = ")
        .append(multiline.trim())
        .append('\n');
    }
    
    boolean hasErrors() {
      return sb.length() > 0;
    }
    
    @Override
    public String toString() {
      return sb.toString();
    }
  }
  
  Set<String> tables = new HashSet<String>();
  final Errors errors = new Errors();
  private Deque<Container> stack = new ArrayDeque<Container>();

  Results() {
    stack.push(new Container.Table());
  }

  void addValue(String key, Object value) {
    Container currentTable = stack.peek();
    if (currentTable.accepts(key)) {
      currentTable.put(key, value);
    } else {
      errors.duplicateKey(key, -1);
    }
  }

  void startTableArray(String tableName) {
    while (stack.size() > 1) {
      stack.pop();
    }

    Keys.Key[] tableParts = Keys.split(tableName);
    for (int i = 0; i < tableParts.length; i++) {
      String tablePart = tableParts[i].name;
      Container currentContainer = stack.peek();

      if (currentContainer.get(tablePart) instanceof Container.TableArray) {
        Container.TableArray currentTableArray = (Container.TableArray) currentContainer.get(tablePart);
        stack.push(currentTableArray);

        if (i == tableParts.length - 1) {
          currentTableArray.put(tablePart, new Container.Table());
        }

        stack.push(currentTableArray.getCurrent());
        currentContainer = stack.peek();
      } else if (currentContainer.get(tablePart) instanceof Container.Table && i < tableParts.length - 1) {
        Container nextTable = (Container) currentContainer.get(tablePart);
        stack.push(nextTable);
      } else if (currentContainer.accepts(tablePart)) {
        Container newContainer = i == tableParts.length - 1 ? new Container.TableArray() : new Container.Table();
        addValue(tablePart, newContainer);
        stack.push(newContainer);

        if (newContainer instanceof Container.TableArray) {
          stack.push(((Container.TableArray) newContainer).getCurrent());
        }
      } else {
        errors.duplicateTable(tableName, -1);
        break;
      }
    }
  }

  void startTables(String tableName) {
    if (!tables.add(tableName)) {
      errors.duplicateTable(tableName, -1);
    }
    
    if (tableName.endsWith(".")) {
      errors.emptyImplicitTable(tableName, -1);
    }

    while (stack.size() > 1) {
      stack.pop();
    }

    Keys.Key[] tableParts = Keys.split(tableName);
    for (int i = 0; i < tableParts.length; i++) {
      String tablePart = tableParts[i].name;
      Container currentContainer = stack.peek();
      if (tablePart.isEmpty()) {
        errors.emptyImplicitTable(tableName, -1);
      } else if (currentContainer.get(tablePart) instanceof Container) {
        Container nextTable = (Container) currentContainer.get(tablePart);
        stack.push(nextTable);
        if (stack.peek() instanceof Container.TableArray) {
          stack.push(((Container.TableArray) stack.peek()).getCurrent());
        }
      } else if (currentContainer.accepts(tablePart)) {
        startTable(tablePart);
      } else {
        errors.duplicateTable(tableName, -1);
        break;
      }
    }
  }

  /**
   * Warning: After this method has been called, this instance is no longer usable.
   */
  Map<String, Object> consume() {
    Container values = stack.getLast();
    stack.clear();

    return ((Container.Table) values).consume();
  }

  private Container startTable(String tableName) {
    Container newTable = new Container.Table();
    addValue(tableName, newTable);
    stack.push(newTable);

    return newTable;
  }
}
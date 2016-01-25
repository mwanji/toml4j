package com.moandjiezana.toml;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class Results {
  
  static class Errors {
    
    private final StringBuilder sb = new StringBuilder();
    
    void duplicateTable(String table, int line) {
      sb.append("Duplicate table definition on line ")
        .append(line)
        .append(": [")
        .append(table)
        .append("]");
    }

    public void tableDuplicatesKey(String table, AtomicInteger line) {
      sb.append("Key already exists for table defined on line ")
        .append(line.get())
        .append(": [")
        .append(table)
        .append("]");
    }

    public void keyDuplicatesTable(String key, AtomicInteger line) {
      sb.append("Table already exists for key defined on line ")
        .append(line.get())
        .append(": ")
        .append(key);
    }
    
    void emptyImplicitTable(String table, int line) {
      sb.append("Invalid table definition due to empty implicit table name: ")
        .append(table);
    }
    
    void invalidTable(String table, int line) {
      sb.append("Invalid table definition on line ")
        .append(line)
        .append(": ")
        .append(table)
        .append("]");
    }
    
    void duplicateKey(String key, int line) {
      sb.append("Duplicate key");
      if (line > -1) {
        sb.append(" on line ")
          .append(line);
      }
      sb.append(": ")
        .append(key);
    }
    
    void invalidTextAfterIdentifier(Identifier identifier, char text, int line) {
      sb.append("Invalid text after key ")
        .append(identifier.getName())
        .append(" on line ")
        .append(line)
        .append(". Make sure to terminate the value or add a comment (#).");
    }
    
    void invalidKey(String key, int line) {
      sb.append("Invalid key on line ")
        .append(line)
        .append(": ")
        .append(key);
    }
    
    void invalidTableArray(String tableArray, int line) {
      sb.append("Invalid table array definition on line ")
        .append(line)
        .append(": ")
        .append(tableArray);
    }
    
    void invalidValue(String key, String value, int line) {
      sb.append("Invalid value on line ")
        .append(line)
        .append(": ")
        .append(key)
        .append(" = ")
        .append(value);
    }
    
    void unterminatedKey(String key, int line) {
      sb.append("Key is not followed by an equals sign on line ")
        .append(line)
        .append(": ")
        .append(key);
    }
    
    void unterminated(String key, String value, int line) {
      sb.append("Unterminated value on line ")
        .append(line)
        .append(": ")
        .append(key)
        .append(" = ")
        .append(value.trim());
    }

    public void heterogenous(String key, int line) {
      sb.append(key)
        .append(" becomes a heterogeneous array on line ")
        .append(line);
    }
    
    boolean hasErrors() {
      return sb.length() > 0;
    }
    
    @Override
    public String toString() {
      return sb.toString();
    }

    public void add(Errors other) {
      sb.append(other.sb);
    }
  }
  
  final Errors errors = new Errors();
  private final Set<String> tables = new HashSet<String>();
  private final Deque<Container> stack = new ArrayDeque<Container>();

  Results() {
    stack.push(new Container.Table(""));
  }

  void addValue(String key, Object value, AtomicInteger line) {
    Container currentTable = stack.peek();
    
    if (value instanceof Map) {
      String path = getInlineTablePath(key);
      if (path == null) {
        startTable(key, line);
      } else if (path.isEmpty()) {
        startTables(Identifier.from(key, null), line);
      } else {
        startTables(Identifier.from(path, null), line);
      }
      @SuppressWarnings("unchecked")
      Map<String, Object> valueMap = (Map<String, Object>) value;
      for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
        addValue(entry.getKey(), entry.getValue(), line);
      }
      stack.pop();
    } else if (currentTable.accepts(key)) {
      currentTable.put(key, value);
    } else {
      if (currentTable.get(key) instanceof Container) {
        errors.keyDuplicatesTable(key, line);
      } else {
        errors.duplicateKey(key, line != null ? line.get() : -1);
      }
    }
  }

  void startTableArray(Identifier identifier, AtomicInteger line) {
    String tableName = identifier.getBareName();
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
        addValue(tablePart, newContainer, line);
        stack.push(newContainer);

        if (newContainer instanceof Container.TableArray) {
          stack.push(((Container.TableArray) newContainer).getCurrent());
        }
      } else {
        errors.duplicateTable(tableName, line.get());
        break;
      }
    }
  }

  void startTables(Identifier id, AtomicInteger line) {
    String tableName = id.getBareName();
    
    while (stack.size() > 1) {
      stack.pop();
    }

    Keys.Key[] tableParts = Keys.split(tableName);
    for (int i = 0; i < tableParts.length; i++) {
      String tablePart = tableParts[i].name;
      Container currentContainer = stack.peek();
      if (currentContainer.get(tablePart) instanceof Container) {
        Container nextTable = (Container) currentContainer.get(tablePart);
        if (i == tableParts.length - 1 && !nextTable.isImplicit()) {
          errors.duplicateTable(tableName, line.get());
          return;
        }
        stack.push(nextTable);
        if (stack.peek() instanceof Container.TableArray) {
          stack.push(((Container.TableArray) stack.peek()).getCurrent());
        }
      } else if (currentContainer.accepts(tablePart)) {
        startTable(tablePart, i < tableParts.length - 1, line);
      } else {
        errors.tableDuplicatesKey(tablePart, line);
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

  private Container startTable(String tableName, AtomicInteger line) {
    Container newTable = new Container.Table(tableName);
    addValue(tableName, newTable, line);
    stack.push(newTable);

    return newTable;
  }

  private Container startTable(String tableName, boolean implicit, AtomicInteger line) {
    Container newTable = new Container.Table(tableName, implicit);
    addValue(tableName, newTable, line);
    stack.push(newTable);

    return newTable;
  }
  
  private String getInlineTablePath(String key) {
    Iterator<Container> descendingIterator = stack.descendingIterator();
    StringBuilder sb = new StringBuilder();
    
    while (descendingIterator.hasNext()) {
      Container next = descendingIterator.next();
      if (next instanceof Container.TableArray) {
        return null;
      }
      
      Container.Table table = (Container.Table) next;
      
      if (table.name == null) {
        break;
      }
      
      if (sb.length() > 0) {
        sb.append('.');
      }
      
      sb.append(table.name);
    }
    
    if (sb.length() > 0) {
      sb.append('.');
    }

    sb.append(key)
      .insert(0, '[')
      .append(']');
    
    return sb.toString();
  }
}
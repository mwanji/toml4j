package com.moandjiezana.toml;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Results {
  public Set<String> tables = new HashSet<String>();
  public StringBuilder errors = new StringBuilder();
  private Deque<Container> stack = new ArrayDeque<Container>();

  public Results() {
    stack.push(new Container.Table());
  }

  public void addValue(String key, Object value) {
    Container currentTable = stack.peek();
    if (currentTable.accepts(key)) {
      currentTable.put(key, value);
    } else {
      errors.append("Key " + key + " is defined twice!\n");
    }
  }

  public void startTableArray(String tableName) {
    while (stack.size() > 1) {
      stack.pop();
    }

    String[] tableParts = tableName.split("\\.");
    for (int i = 0; i < tableParts.length; i++) {
      String tablePart = tableParts[i];
      Container currentContainer = stack.peek();

      if (currentContainer.get(tablePart) instanceof Container.TableArray) {
        Container.TableArray currentTableArray = (Container.TableArray) currentContainer.get(tablePart);
        stack.push(currentTableArray);

        if (i == tableParts.length - 1) {
          currentTableArray.put(tablePart, new Container.Table());
        }

        stack.push(currentTableArray.getCurrent());
        currentContainer = stack.peek();
      } else if (currentContainer.get(tablePart) instanceof Container.Table) {
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
        errors.append("Duplicate key and table definitions for " + tableName + "!\n");
        break;
      }
    }
  }

  public void startTables(String tableName) {
    if (!tables.add(tableName)) {
      errors.append("Table " + tableName + " defined twice!\n");
    }

    while (stack.size() > 1) {
      stack.pop();
    }

    String[] tableParts = tableName.split("\\.");
    for (int i = 0; i < tableParts.length; i++) {
      String tablePart = tableParts[i];
      Container currentContainer = stack.peek();
      if (currentContainer.get(tablePart) instanceof Container) {
        Container nextTable = (Container) currentContainer.get(tablePart);
        stack.push(nextTable);
        if (stack.peek() instanceof Container.TableArray) {
          stack.push(((Container.TableArray) stack.peek()).getCurrent());
        }
      } else if (currentContainer.accepts(tablePart)) {
        startTable(tablePart);
      } else {
        errors.append("Duplicate key and table definitions for " + tableName + "!\n");
        break;
      }
    }
  }

  /**
   * Warning: After this method has been called, this instance is no longer usable.
   */
  public Map<String, Object> consume() {
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
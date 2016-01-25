package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class Container {

  abstract boolean accepts(String key);
  abstract void put(String key, Object value);
  abstract Object get(String key);
  abstract boolean isImplicit();

  static class Table extends Container {
    private final Map<String, Object> values = new HashMap<String, Object>();
    final String name;
    final boolean implicit;

    Table() {
      this(null, false);
    }
    
    public Table(String name) {
      this(name, false);
    }

    public Table(String tableName, boolean implicit) {
      this.name = tableName;
      this.implicit = implicit;
    }

    @Override
    boolean accepts(String key) {
      return !values.containsKey(key) || values.get(key) instanceof Container.TableArray;
    }

    @Override
    void put(String key, Object value) {
      values.put(key, value);
    }

    @Override
    Object get(String key) {
      return values.get(key);
    }
    
    boolean isImplicit() {
      return implicit;
    }

    /**
     * This modifies the Table's internal data structure, such that it is no longer usable.
     *
     * Therefore, this method must only be called when all data has been gathered.

     * @return A Map-and-List-based of the TOML data
     */
    Map<String, Object> consume() {
      for (Map.Entry<String, Object> entry : values.entrySet()) {
        if (entry.getValue() instanceof Container.Table) {
          entry.setValue(((Container.Table) entry.getValue()).consume());
        } else if (entry.getValue() instanceof Container.TableArray) {
          entry.setValue(((Container.TableArray) entry.getValue()).getValues());
        }
      }

      return values;
    }

    @Override
    public String toString() {
      return values.toString();
    }
  }

  static class TableArray extends Container {
    private final List<Container.Table> values = new ArrayList<Container.Table>();

    TableArray() {
      values.add(new Container.Table());
    }

    @Override
    boolean accepts(String key) {
      return getCurrent().accepts(key);
    }

    @Override
    void put(String key, Object value) {
      values.add((Container.Table) value);
    }

    @Override
    Object get(String key) {
      throw new UnsupportedOperationException();
    }
    
    boolean isImplicit() {
      return false;
    }

    List<Map<String, Object>> getValues() {
      ArrayList<Map<String, Object>> unwrappedValues = new ArrayList<Map<String,Object>>();
      for (Container.Table table : values) {
        unwrappedValues.add(table.consume());
      }
      return unwrappedValues;
    }

    Container.Table getCurrent() {
      return values.get(values.size() - 1);
    }

    @Override
    public String toString() {
      return values.toString();
    }
  }

  private Container() {}
}

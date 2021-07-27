package com.moandjiezana.toml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * <p>Provides access to the keys and tables in a TOML data source.</p>
 *
 * <p>All getters can fall back to default values if they have been provided as a constructor argument.
 * Getters for simple values (String, Date, etc.) will return null if no matching key exists.
 * {@link #getList(String)}, {@link #getTable(String)} and {@link #getTables(String)} return empty values if there is no matching key.</p>
 * 
 * <p>All read methods throw an {@link IllegalStateException} if the TOML is incorrect.</p>
 *
 * <p>Example usage:</p>
 * <pre><code>
 * Toml toml = new Toml().read(getTomlFile());
 * String name = toml.getString("name");
 * Long port = toml.getLong("server.ip"); // compound key. Is equivalent to:
 * Long port2 = toml.getTable("server").getLong("ip");
 * MyConfig config = toml.to(MyConfig.class);
 * </code></pre>
 *
 */
public class Toml {
  
  private static final Gson DEFAULT_GSON = new Gson();

  private Map<String, Object> values = new HashMap<String, Object>();
  private final Toml defaults;

  /**
   * Creates Toml instance with no defaults.
   */
  public Toml() {
    this(null);
  }

  /**
   * @param defaults fallback values used when the requested key or table is not present in the TOML source that has been read.
   */
  public Toml(Toml defaults) {
    this(defaults, new HashMap<String, Object>());
  }

  /**
   * Populates the current Toml instance with values from file.
   *
   * @param file The File to be read. Expected to be encoded as UTF-8.
   * @return this instance
   * @throws IllegalStateException If file contains invalid TOML
   */
  public Toml read(File file) {
    try {
      return read(new InputStreamReader(new FileInputStream(file), "UTF8"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Populates the current Toml instance with values from inputStream.
   *
   * @param inputStream Closed after it has been read.
   * @return this instance
   * @throws IllegalStateException If file contains invalid TOML
   */
  public Toml read(InputStream inputStream) {
    return read(new InputStreamReader(inputStream));
  }

  /**
   * Populates the current Toml instance with values from reader.
   *
   * @param reader Closed after it has been read.
   * @return this instance
   * @throws IllegalStateException If file contains invalid TOML
   */
  public Toml read(Reader reader) {
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(reader);

      StringBuilder w = new StringBuilder();
      String line = bufferedReader.readLine();
      while (line != null) {
        w.append(line).append('\n');
        line = bufferedReader.readLine();
      }
      read(w.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        bufferedReader.close();
      } catch (IOException e) {}
    }
    return this;
  }

  /**
   * Populates the current Toml instance with values from otherToml.
   *
   * @param otherToml 
   * @return this instance
   */
  public Toml read(Toml otherToml) {
    this.values = otherToml.values;
    
    return this;
  }

  /**
   * Populates the current Toml instance with values from tomlString.
   *
   * @param tomlString String to be read.
   * @return this instance
   * @throws IllegalStateException If tomlString is not valid TOML
   */
  public Toml read(String tomlString) throws IllegalStateException {
    Results results = TomlParser.run(tomlString);
    if (results.errors.hasErrors()) {
      throw new IllegalStateException(results.errors.toString());
    }

    this.values = results.consume();

    return this;
  }

  public String getString(String key) {
    return (String) get(key);
  }

  public String getString(String key, String defaultValue) {
    String val = getString(key);
    return val == null ? defaultValue : val;
  }

  public Long getLong(String key) {
    return (Long) get(key);
  }

  public Long getLong(String key, Long defaultValue) {
    Long val = getLong(key);
    return val == null ? defaultValue : val;
  }

  public Integer getInteger(String key) {
      Long val = getLong(key);
      return val == null ? null : val.intValue();
  }

  public Integer getInteger(String key, Integer defaultValue) {
      Integer val = getInteger(key);
      return val == null ? defaultValue : val;
  }

  /**
   * @param key a TOML key
   * @param <T> type of list items
   * @return <code>null</code> if the key is not found
   */
  public <T> List<T> getList(String key) {
    @SuppressWarnings("unchecked")
    List<T> list = (List<T>) get(key);
    
    return list;
  }

  /**
   * @param key a TOML key
   * @param defaultValue a list of default values
   * @param <T> type of list items
   * @return <code>null</code> is the key is not found
   */
  public <T> List<T> getList(String key, List<T> defaultValue) {
    List<T> list = getList(key);
    
    return list != null ? list : defaultValue;
  }

  public Boolean getBoolean(String key) {
    return (Boolean) get(key);
  }

  public Boolean getBoolean(String key, Boolean defaultValue) {
    Boolean val = getBoolean(key);
    return val == null ? defaultValue : val;
  }

  public Date getDate(String key) {
    return (Date) get(key);
  }

  public Date getDate(String key, Date defaultValue) {
    Date val = getDate(key);
    return val == null ? defaultValue : val;
  }

  public Double getDouble(String key) {
    return (Double) get(key);
  }

  public Double getDouble(String key, Double defaultValue) {
    Double val = getDouble(key);
    return val == null ? defaultValue : val;
  }

  /**
   * @param key A table name, not including square brackets.
   * @return A new Toml instance or <code>null</code> if no value is found for key.
   */
  @SuppressWarnings("unchecked")
  public Toml getTable(String key) {
    Map<String, Object> map = (Map<String, Object>) get(key);
    
    return map != null ? new Toml(null, map) : null;
  }

  /**
   * @param key Name of array of tables, not including square brackets.
   * @return A {@link List} of Toml instances or <code>null</code> if no value is found for key.
   */
  @SuppressWarnings("unchecked")
  public List<Toml> getTables(String key) {
    List<Map<String, Object>> tableArray = (List<Map<String, Object>>) get(key);

    if (tableArray == null) {
      return null;
    }

    ArrayList<Toml> tables = new ArrayList<Toml>();

    for (Map<String, Object> table : tableArray) {
      tables.add(new Toml(null, table));
    }

    return tables;
  }

  /**
   * @param key a key name, can be compound (eg. a.b.c)
   * @return true if key is present
   */
  public boolean contains(String key) {
    return get(key) != null;
  }

  /**
   * @param key a key name, can be compound (eg. a.b.c)
   * @return true if key is present and is a primitive
   */
  public boolean containsPrimitive(String key) {
    Object object = get(key);
    
    return object != null && !(object instanceof Map) && !(object instanceof List);
  }

  /**
   * @param key a key name, can be compound (eg. a.b.c)
   * @return true if key is present and is a table
   */
  public boolean containsTable(String key) {
    Object object = get(key);
    
    return object != null && (object instanceof Map);
  }

  /**
   * @param key a key name, can be compound (eg. a.b.c)
   * @return true if key is present and is a table array
   */
  public boolean containsTableArray(String key) {
    Object object = get(key);
    
    return object != null && (object instanceof List);
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  /**
   * <p>
   *  Populates an instance of targetClass with the values of this Toml instance.
   *  The target's field names must match keys or tables.
   *  Keys not present in targetClass will be ignored.
   * </p>
   *
   * <p>Tables are recursively converted to custom classes or to {@link Map Map&lt;String, Object&gt;}.</p>
   *
   * <p>In addition to straight-forward conversion of TOML primitives, the following are also available:</p>
   *
   * <ul>
   *  <li>Integer -&gt; int, long (or wrapper), {@link java.math.BigInteger}</li>
   *  <li>Float -&gt; float, double (or wrapper), {@link java.math.BigDecimal}</li>
   *  <li>One-letter String -&gt; char, {@link Character}</li>
   *  <li>String -&gt; {@link String}, enum, {@link java.net.URI}, {@link java.net.URL}</li>
   *  <li>Multiline and Literal Strings -&gt; {@link String}</li>
   *  <li>Array -&gt; {@link List}, {@link Set}, array. The generic type can be anything that can be converted.</li>
   *  <li>Table -&gt; Custom class, {@link Map Map&lt;String, Object&gt;}</li>
   * </ul>
   *
   * @param targetClass Class to deserialize TOML to.
   * @param <T> type of targetClass.
   * @return A new instance of targetClass.
   */
  public <T> T to(Class<T> targetClass) {
    JsonElement json = DEFAULT_GSON.toJsonTree(toMap());
    
    if (targetClass == JsonElement.class) {
      return targetClass.cast(json);
    }
    
    return DEFAULT_GSON.fromJson(json, targetClass);
  }

  public Map<String, Object> toMap() {
    HashMap<String, Object> valuesCopy = new HashMap<String, Object>(values);
    
    if (defaults != null) {
      for (Map.Entry<String, Object> entry : defaults.values.entrySet()) {
        if (!valuesCopy.containsKey(entry.getKey())) {
          valuesCopy.put(entry.getKey(), entry.getValue());
        }
      }
    }

    return valuesCopy;
  }
  
  /**
   * @return a {@link Set} of Map.Entry instances. Modifications to the {@link Set} are not reflected in this Toml instance. Entries are immutable, so {@link Map.Entry#setValue(Object)} throws an UnsupportedOperationException.
   */
  public Set<Map.Entry<String,Object>> entrySet() {
    Set<Map.Entry<String, Object>> entries = new LinkedHashSet<Map.Entry<String, Object>>();
    
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      Class<? extends Object> entryClass = entry.getValue().getClass();
      
      if (Map.class.isAssignableFrom(entryClass)) {
        entries.add(new Toml.Entry(entry.getKey(), getTable(entry.getKey())));
      } else if (List.class.isAssignableFrom(entryClass)) {
        List<?> value = (List<?>) entry.getValue();
        if (!value.isEmpty() && value.get(0) instanceof Map) {
          entries.add(new Toml.Entry(entry.getKey(), getTables(entry.getKey())));
        } else {
          entries.add(new Toml.Entry(entry.getKey(), value));
        }
      } else {
        entries.add(new Toml.Entry(entry.getKey(), entry.getValue()));
      }
    }
    
    return entries;
  }

  private class Entry implements Map.Entry<String, Object> {
    
    private final String key;
    private final Object value;

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public Object getValue() {
      return value;
    }

    @Override
    public Object setValue(Object value) {
      throw new UnsupportedOperationException("TOML entry values cannot be changed.");
    }
    
    private Entry(String key, Object value) {
      this.key = key;
      this.value = value;
    }
  }

  @SuppressWarnings("unchecked")
  private Object get(String key) {
    if (values.containsKey(key)) {
      return values.get(key);
    }

    Object current = new HashMap<String, Object>(values);
    
    Keys.Key[] keys = Keys.split(key);
    
    for (Keys.Key k : keys) {
      if (k.index == -1 && current instanceof Map && ((Map<String, Object>) current).containsKey(k.path)) {
        return ((Map<String, Object>) current).get(k.path);
      }

      current = ((Map<String, Object>) current).get(k.name);

      if (k.index > -1 && current != null) {
        if (k.index >= ((List<?>) current).size()) {
          return null;
        }
        
        current = ((List<?>) current).get(k.index);
      }

      if (current == null) {
        return defaults != null ? defaults.get(key) : null;
      }
    }
    
    return current;
  }
  
  private Toml(Toml defaults, Map<String, Object> values) {
    this.values = values;
    this.defaults = defaults;
  }
}

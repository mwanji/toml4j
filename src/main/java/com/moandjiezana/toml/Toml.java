package com.moandjiezana.toml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * <p>Provides access to the keys and tables in a TOML data source.</p>
 *
 * <p>All getters can fall back to default values if they have been provided.
 * Getters for simple values (String, Date, etc.) will return null if no matching key exists.
 * {@link #getList(String, Class)}, {@link #getTable(String)} and {@link #getTables(String)} return empty values if there is no matching key.</p>
 *
 * <p>Example usage:</p>
 * <pre><code>
 * Toml toml = new Toml().parse(getTomlFile());
 * String name = toml.getString("name");
 * Long port = toml.getLong("server.ip"); // compound key. Is equivalent to:
 * Long port2 = toml.getTable("server").getLong("ip");
 * MyConfig config = toml.to(MyConfig.class);
 * </code></pre>
 *
 */
public class Toml {

  private static final Gson DEFAULT_GSON = new Gson();
  private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("(.*)\\[(\\d+)\\]");

  private Map<String, Object> values = new HashMap<String, Object>();
  private final Toml defaults;

  /**
   * Creates Toml instance with no defaults.
   */
  public Toml() {
    this(null);
  }

  /**
   * @param defaults fallback values used when the requested key or table is not present.
   */
  public Toml(Toml defaults) {
    this(defaults, new HashMap<String, Object>());
  }

  /**
   * Populates the current Toml instance with values from file.
   *
   * @param file The File to be read
   * @return this instance
   * @throws IllegalStateException If file contains invalid TOML
   */
  public Toml parse(File file) {
    try {
      return parse(new FileReader(file));
    } catch (FileNotFoundException e) {
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
  public Toml parse(InputStream inputStream) {
    return parse(new InputStreamReader(inputStream));
  }

  /**
   * Populates the current Toml instance with values from reader.
   *
   * @param reader Closed after it has been read.
   * @return this instance
   * @throws IllegalStateException If file contains invalid TOML
   */
  public Toml parse(Reader reader) {
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(reader);

      StringBuilder w = new StringBuilder();
      String line = bufferedReader.readLine();
      while (line != null) {
        w.append(line).append('\n');
        line = bufferedReader.readLine();
      }
      parse(w.toString());
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
   * Populates the current Toml instance with values from tomlString.
   *
   * @param tomlString String to be read.
   * @return this instance
   * @throws IllegalStateException If tomlString is not valid TOML
   */
  public Toml parse(String tomlString) throws IllegalStateException {
    Results results = new TomlParser().run(tomlString);
    if (results.errors.length() > 0) {
      throw new IllegalStateException(results.errors.toString());
    }

    this.values = results.consume();

    return this;
  }

  public String getString(String key) {
    return (String) get(key);
  }

  public Long getLong(String key) {
    return (Long) get(key);
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getList(String key, Class<T> itemClass) {
    List<T> list = (List<T>) get(key);

    if (list == null) {
      return Collections.emptyList();
    }

    return list;
  }

  public Boolean getBoolean(String key) {
    return (Boolean) get(key);
  }

  public Date getDate(String key) {
    return (Date) get(key);
  }

  public Double getDouble(String key) {
    return (Double) get(key);
  }

  /**
   * @param key A table name, not including square brackets.
   * @return A new Toml instance. Empty if no value is found for key.
   */
  @SuppressWarnings("unchecked")
  public Toml getTable(String key) {
    return new Toml(null, (Map<String, Object>) get(key));
  }

  /**
   * @param key Name of array of tables, not including square brackets.
   * @return An empty List if no value is found for key.
   */
  @SuppressWarnings("unchecked")
  public List<Toml> getTables(String key) {
    List<Map<String, Object>> tableArray = (List<Map<String, Object>>) get(key);

    if (tableArray == null) {
      return Collections.emptyList();
    }

    ArrayList<Toml> tables = new ArrayList<Toml>();

    for (Map<String, Object> table : tableArray) {
      tables.add(new Toml(null, table));
    }

    return tables;
  }

  /**
   * <p>Populates an instance of targetClass with the values of this Toml instance.
   * The target's field names must match keys or tables.
   * Keys not present in targetClass will be ignored.</p>
   *
   * <p>Tables are recursively converted to custom classes or to {@link Map Map&lt;String, Object&gt;}.</p>
   *
   * <p>In addition to straight-forward conversion of TOML primitives, the following are also available:</p>
   *
   * <ul>
   *  <li>TOML string to {@link Character}, {@link URL} or enum</li>
   *  <li>TOML number to any primitive (or wrapper), {@link BigInteger} or {@link BigDecimal}</li>
   *  <li>TOML array to {@link Set}</li>
   * </ul>
   *
   * @param targetClass Class to deserialize TOML to.
   * @param <T> type of targetClass.
   * @return A new instance of targetClass.
   */
  public <T> T to(Class<T> targetClass) {
    return to(targetClass, DEFAULT_GSON);
  }

  /*
   * Should not be used directly, except for testing purposes
   */
  <T> T to(Class<T> targetClass, Gson gson) {
    HashMap<String, Object> valuesCopy = new HashMap<String, Object>(values);

    if (defaults != null) {
      for (Map.Entry<String, Object> entry : defaults.values.entrySet()) {
        if (!valuesCopy.containsKey(entry.getKey())) {
          valuesCopy.put(entry.getKey(), entry.getValue());
        }
      }
    }

    JsonElement json = gson.toJsonTree(valuesCopy);

    if (targetClass == JsonElement.class) {
      return targetClass.cast(json);
    }

    return gson.fromJson(json, targetClass);
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
        current = ((List<?>) current).get(k.index);
      }

      if (current == null) {
        return defaults != null ? defaults.get(key) : null;
      }
    }
    
    return current;
  }

  private Toml(Toml defaults, Map<String, Object> values) {
    this.values = values != null ? values : Collections.<String, Object>emptyMap();
    this.defaults = defaults;
  }
}

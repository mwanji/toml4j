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

import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import com.google.gson.Gson;

/**
 * <p>Provides access to the keys and tables in a TOML data source.</p>
 *
 * <p>All getters can fall back to default values if they have been provided.
 * Getters for simple values (String, Date, etc.) will return null if no matching key exists.
 * {@link #getList(String, Class)}, {@link #getTable(String)} and {@link #getTables(String)} return empty values if there is no matching key.</p>
 *
 * <p>Example usage:</p>
 * <code><pre>
 * Toml toml = new Toml().parse(getTomlFile());
 * String name = toml.getString("name");
 * Long port = toml.getLong("server.ip"); // compound key. Is equivalent to:
 * Long port2 = toml.getTable("server").getLong("ip");
 * MyConfig config = toml.to(MyConfig.class);
 * </pre></code>
 *
 */
public class Toml {

  private Map<String, Object> values = new HashMap<String, Object>();
  private final Toml defaults;

  /**
   * Creates Toml instance with no defaults.
   */
  public Toml() {
    this((Toml) null);
  }

  /**
   * @param defaults fallback values used when the requested key or table is not present.
   */
  public Toml(Toml defaults) {
    this.defaults = defaults;
  }

  /**
   * Populates the current Toml instance with values from file.
   *
   * @param file
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
   * @param inputStream
   * @return this instance
   * @throws IllegalStateException If file contains invalid TOML
   */
  public Toml parse(InputStream inputStream) {
    return parse(new InputStreamReader(inputStream));
  }

  /**
   * Populates the current Toml instance with values from reader.
   *
   * @param reader
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
   * @param tomlString
   * @return this instance
   * @throws IllegalStateException If tomlString is not valid TOML
   */
  public Toml parse(String tomlString) throws IllegalStateException {
    TomlParser parser = Parboiled.createParser(TomlParser.class);
    ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.Toml()).run(tomlString);
//    ParsingResult<Object> parsingResult = new ReportingParseRunner<Object>(parser.Toml()).run(tomlString);
//    System.out.println(ParseTreeUtils.printNodeTree(parsingResult));

    TomlParser.Results results = (TomlParser.Results) result.valueStack.peek(result.valueStack.size() - 1);
    if (results.errors.length() > 0) {
      throw new IllegalStateException(results.errors.toString());
    }

    this.values = results.values;

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
   * If no value is found for key, an empty Toml instance is returned.
   *
   * @param key
   */
  @SuppressWarnings("unchecked")
  public Toml getTable(String key) {
    return new Toml((Map<String, Object>) get(key));
  }

  /**
   * If no value is found for key, an empty list is returned.
   * @param key
   */
  @SuppressWarnings("unchecked")
  public List<Toml> getTables(String key) {
    List<Map<String, Object>> tableArray = (List<Map<String, Object>>) get(key);

    if (tableArray == null) {
      return Collections.emptyList();
    }

    ArrayList<Toml> tables = new ArrayList<Toml>();
    for (Map<String, Object> table : tableArray) {
      tables.add(new Toml(table));
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
   * @param targetClass
   */
  public <T> T to(Class<T> targetClass) {
    HashMap<String, Object> valuesCopy = new HashMap<String, Object>(values);
    if (defaults != null) {
      for (Map.Entry<String, Object> entry : defaults.values.entrySet()) {
        if (!valuesCopy.containsKey(entry.getKey())) {
          valuesCopy.put(entry.getKey(), entry.getValue());
        }
      }
    }
    Gson gson = new Gson();
    String json = gson.toJson(valuesCopy);
    return gson.fromJson(json, targetClass);
  }

  @SuppressWarnings("unchecked")
  private Object get(String key) {
    String[] split = key.split("\\.");
    Object current = new HashMap<String, Object>(values);
    Object currentDefaults = defaults != null ? defaults.values : null;
    for (String splitKey : split) {
      current = ((Map<String, Object>) current).get(splitKey);
      if (currentDefaults != null) {
        currentDefaults = ((Map<String, Object>) currentDefaults).get(splitKey);
        if (current instanceof Map && currentDefaults instanceof Map) {
          for (Map.Entry<String, Object> entry : ((Map<String, Object>) currentDefaults).entrySet()) {
            if (!((Map<String, Object>) current).containsKey(entry.getKey())) {
              ((Map<String, Object>) current).put(entry.getKey(), entry.getValue());
            }
          }
        }
      }
      if (current == null && currentDefaults != null) {
        current = currentDefaults;
      }
      if (current == null) {
        return null;
      }
    }

    return current;
  }

  private Toml(Map<String, Object> values) {
    this.values = values != null ? values : Collections.<String, Object>emptyMap();
    this.defaults = null;
  }
}

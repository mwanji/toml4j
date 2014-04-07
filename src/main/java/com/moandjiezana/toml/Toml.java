package com.moandjiezana.toml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import com.google.gson.Gson;

/**
 *
 * All getters can fall back to default values if they have been provided and will return null if no matching key exists.
 *
 */
public class Toml {

  private Map<String, Object> values = new HashMap<String, Object>();
  private final Toml defaults;

  public Toml() {
    this((Toml) null);
  }

  public Toml(Toml defaults) {
    this.defaults = defaults;
  }

  public Toml parse(File file) {
    try {
      return parse(new Scanner(file).useDelimiter("\\Z").next());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Toml parse(String tomlString) {
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
    return (List<T>) get(key);
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

  @SuppressWarnings("unchecked")
  public Toml getTable(String key) {
    return new Toml((Map<String, Object>) get(key));
  }

  @SuppressWarnings("unchecked")
  public List<Toml> getTables(String key) {
    ArrayList<Toml> tables = new ArrayList<Toml>();
    for (Map<String, Object> table : (List<Map<String, Object>>) get(key)) {
      tables.add(new Toml(table));
    }

    return tables;
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
    this.values = values;
    this.defaults = null;
  }

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
}

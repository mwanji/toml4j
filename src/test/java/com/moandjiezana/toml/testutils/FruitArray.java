package com.moandjiezana.toml.testutils;

import java.util.List;
import java.util.Map;

public class FruitArray {

  public static class Fruit {
    public String name;
    public Physical physical;
    public List<Map<String, String>> variety;
  }

  public static class Physical {
    public String color;
    public String shape;
  }

  public List<Fruit> fruit;
}

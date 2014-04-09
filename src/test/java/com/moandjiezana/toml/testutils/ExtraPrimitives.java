package com.moandjiezana.toml.testutils;

import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class ExtraPrimitives {

  public Map<String, Object> group;
  public BigDecimal bigDecimal;
  public BigInteger bigInteger;
  public short aShort;
  public Integer anInteger;
  public Character character;
  public ElementType elementType;
  public URL url;
  public URI uri;
  public Set<String> set;
  public String[] strings;
}

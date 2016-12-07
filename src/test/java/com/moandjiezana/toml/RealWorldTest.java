package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

public class RealWorldTest {

  @SuppressWarnings("unchecked")
  @Test
  public void should_parse_example() throws Exception {
    File file = new File(getClass().getResource("example.toml").getFile());
    Toml toml = new Toml().read(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF8")));


    // printMap(root);

    assertEquals("TOML Example", toml.getString("title"));

    Toml owner = toml.getTable("owner");
    assertEquals("Tom Preston-Werner", owner.getString("name"));
    assertEquals("GitHub", owner.getString("organization"));
    assertEquals("GitHub Cofounder & CEO\nLikes tater tots and beer.", owner.getString("bio"));

    Calendar dob = Calendar.getInstance();
    dob.set(1979, Calendar.MAY, 27, 7, 32, 0);
    dob.set(Calendar.MILLISECOND, 0);
    dob.setTimeZone(TimeZone.getTimeZone("UTC"));
    assertEquals(dob.getTime(), owner.getDate("dob"));

    Toml database = toml.getTable("database");
    assertEquals("192.168.1.1", database.getString("server"));
    assertEquals(5000L, database.getLong("connection_max").longValue());
    assertTrue(database.getBoolean("enabled"));
    assertEquals(Arrays.asList(8001L, 8001L, 8002L), database.<Long>getList("ports"));

    Toml servers = toml.getTable("servers");
    Toml alphaServers = servers.getTable("alpha");
    assertEquals("10.0.0.1", alphaServers.getString("ip"));
    assertEquals("eqdc10", alphaServers.getString("dc"));
    Toml betaServers = servers.getTable("beta");
    assertEquals("10.0.0.2", betaServers.getString("ip"));
    assertEquals("eqdc10", betaServers.getString("dc"));
    assertEquals("中国", betaServers.getString("country"));

    Toml clients = toml.getTable("clients");
    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.<String>getList("data"));
    assertEquals(asList("alpha", "omega"), clients.<String>getList("hosts"));
  }

  @Test
  public void should_parse_hard_example() throws Exception {
    Toml toml = new Toml().read(new File(getClass().getResource("hard_example.toml").getFile()));

    assertEquals("You'll hate me after this - #", toml.getString("the.test_string"));
    assertEquals(asList("] ", " # "), toml.<String>getList("the.hard.test_array"));
    assertEquals(asList("Test #11 ]proved that", "Experiment #9 was a success"), toml.<String>getList("the.hard.test_array2"));
    assertEquals(" Same thing, but with a string #", toml.getString("the.hard.another_test_string"));
    assertEquals(" And when \"'s are in the string, along with # \"", toml.getString("the.hard.harder_test_string"));
    Toml theHardBit = toml.getTable("the.hard.\"bit#\"");
    assertEquals("You don't think some user won't do that?", theHardBit.getString("\"what?\""));
    assertEquals(asList("]"), theHardBit.<String>getList("multi_line_array"));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void should_parse_current_version_example() throws Exception {
    Toml toml = new Toml().read(new File(getClass().getResource("example-v0.4.0.toml").getFile()));
    
    assertEquals("value", toml.getString("table.key"));
    assertEquals("another value", toml.getString("table.subtable.key"));
    assertNotNull(toml.getTable("x.y.z").getTable("w"));
    assertEquals("Tom", toml.getString("table.inline.name.first"));
    assertEquals("Preston-Werner", toml.getString("table.inline.name.last"));
    assertEquals(1, toml.getLong("table.inline.point.x").intValue());
    assertEquals(2, toml.getLong("table.inline.point.y").intValue());
//    assertEquals("pug", toml.getString("dog.tater.type"));
    assertEquals("I'm a string. \"You can quote me\". Name\tJosé\nLocation\tSF.", toml.getString("string.basic.basic"));
    assertEquals("One\nTwo", toml.getString("string.multiline.key3"));
    assertEquals(toml.getString("string.multiline.key3"), toml.getString("string.multiline.key1"));
    assertEquals(toml.getString("string.multiline.key3"), toml.getString("string.multiline.key2"));
    assertEquals("The quick brown fox jumps over the lazy dog.", toml.getString("string.multiline.continued.key1"));
    assertEquals("The quick brown fox jumps over the lazy dog.", toml.getString("string.multiline.continued.key2"));
    assertEquals(toml.getString("string.multilined.singleline.key3"), toml.getString("string.multilined.singleline.key1"));
    assertEquals(toml.getString("string.multilined.singleline.key3"), toml.getString("string.multilined.singleline.key2"));
    assertEquals("C:\\Users\\nodejs\\templates", toml.getString("string.literal.winpath"));
    assertEquals("\\\\ServerX\\admin$\\system32\\", toml.getString("string.literal.winpath2"));
    assertEquals("Tom \"Dubs\" Preston-Werner", toml.getString("string.literal.quoted"));
    assertEquals("<\\i\\c*\\s*>", toml.getString("string.literal.regex"));
    assertEquals("I [dw]on't need \\d{2} apples", toml.getString("string.literal.multiline.regex2"));
    assertEquals("The first newline is\ntrimmed in raw strings.\n   All other whitespace\n   is preserved.\n", toml.getString("string.literal.multiline.lines"));
    assertEquals(99, toml.getLong("integer.key1").intValue());
    assertEquals(42, toml.getLong("integer.key2").intValue());
    assertEquals(0, toml.getLong("integer.key3").intValue());
    assertEquals(-17, toml.getLong("integer.key4").intValue());
    assertEquals(1000, toml.getLong("integer.underscores.key1").intValue());
    assertEquals(5349221, toml.getLong("integer.underscores.key2").longValue());
    assertEquals(12345, toml.getLong("integer.underscores.key3").intValue());
    assertEquals(1.0, toml.getDouble("float.fractional.key1").doubleValue(), 0);
    assertEquals(3.1415, toml.getDouble("float.fractional.key2").doubleValue(), 0);
    assertEquals(-0.01, toml.getDouble("float.fractional.key3").doubleValue(), 0);
    assertEquals(5e+22, toml.getDouble("float.exponent.key1").doubleValue(), 0);
    assertEquals(1e6, toml.getDouble("float.exponent.key2").longValue(), 0);
    assertEquals(-2E-2, toml.getDouble("float.exponent.key3").doubleValue(), 0);
    assertEquals(6.626e-34, toml.getDouble("float.both.key").doubleValue(), 0);
    assertTrue(toml.getBoolean("boolean.True"));
    assertFalse(toml.getBoolean("boolean.False"));
    assertThat(toml.<Long>getList("array.key1"), contains(1L, 2L, 3L));
    assertThat(toml.<String>getList("array.key2"), contains("red", "yellow", "green"));
    assertEquals(asList(asList(1L, 2L), asList(3L, 4L, 5L)), toml.<List<Long>>getList("array.key3"));
    assertEquals(asList(asList(1L, 2L), asList("a", "b", "c")), toml.<List<Long>>getList("array.key4"));
    assertThat(toml.<Long>getList("array.key5"), contains(1L, 2L, 3L));
    assertThat(toml.<Long>getList("array.key6"), contains(1L, 2L));
    assertEquals("Hammer", toml.getString("products[0].name"));
    assertEquals(738594937, toml.getLong("products[0].sku").intValue());
    assertNotNull(toml.getTable("products[1]"));
    assertEquals("Nail", toml.getString("products[2].name"));
    assertEquals(284758393, toml.getLong("products[2].sku").intValue());
    assertEquals("gray", toml.getString("products[2].color"));
    assertEquals("apple", toml.getString("fruit[0].name"));
    assertEquals("red", toml.getString("fruit[0].physical.color"));
    assertEquals("round", toml.getString("fruit[0].physical.shape"));
    assertEquals("red delicious", toml.getString("fruit[0].variety[0].name"));
    assertEquals("granny smith", toml.getString("fruit[0].variety[1].name"));
    assertEquals("banana", toml.getString("fruit[1].name"));
    assertEquals("plantain", toml.getString("fruit[1].variety[0].name"));

    Calendar dob = Calendar.getInstance();
    dob.set(1979, Calendar.MAY, 27, 7, 32, 0);
    dob.set(Calendar.MILLISECOND, 0);
    dob.setTimeZone(TimeZone.getTimeZone("UTC"));
    assertEquals(dob.getTime(), toml.getDate("datetime.key1"));
    assertEquals(dob.getTime(), toml.getDate("datetime.key2"));
    dob.set(Calendar.MILLISECOND, 999);
    assertEquals(dob.getTime(), toml.getDate("datetime.key3"));
  }

  @Test
  public void should_allow_keys_with_same_name_in_different_tables() throws Exception {
    Toml toml = new Toml().read(new File(getClass().getResource("should_allow_keys_with_same_name_in_different_tables.toml").getFile()));

    assertTrue(toml.getTable("siteInfo.local.sh").getBoolean("enable"));
    assertFalse(toml.getTable("siteInfo.localMobile.sh").getBoolean("enable"));
  }
}

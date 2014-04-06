package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;

public class RealWorldTest {

  @SuppressWarnings("unchecked")
  @Test
  public void should_parse_example() throws Exception {
    Toml toml = new Toml().parse(new File(getClass().getResource("example.toml").getFile()));

    // printMap(root);

    assertEquals("TOML Example", toml.getString("title"));

    Toml owner = toml.getKeyGroup("owner");
    assertEquals("Tom Preston-Werner", owner.getString("name"));
    assertEquals("GitHub", owner.getString("organization"));
    assertEquals("GitHub Cofounder & CEO\nLikes tater tots and beer.", owner.getString("bio"));

    Calendar dob = Calendar.getInstance();
    dob.set(1979, Calendar.MAY, 27, 7, 32, 0);
    dob.set(Calendar.MILLISECOND, 0);
    dob.setTimeZone(TimeZone.getTimeZone("UTC"));
    assertEquals(dob.getTime(), owner.getDate("dob"));

    Toml database = toml.getKeyGroup("database");
    assertEquals("192.168.1.1", database.getString("server"));
    assertEquals(5000L, database.getLong("connection_max").longValue());
    assertTrue(database.getBoolean("enabled"));
    assertEquals(Arrays.asList(8001L, 8001L, 8002L), database.getList("ports", Long.class));

    Toml servers = toml.getKeyGroup("servers");
    Toml alphaServers = servers.getKeyGroup("alpha");
    assertEquals("10.0.0.1", alphaServers.getString("ip"));
    assertEquals("eqdc10", alphaServers.getString("dc"));
    Toml betaServers = servers.getKeyGroup("beta");
    assertEquals("10.0.0.2", betaServers.getString("ip"));
    assertEquals("eqdc10", betaServers.getString("dc"));

    Toml clients = toml.getKeyGroup("clients");
    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.getList("data", String.class));
    assertEquals(asList("alpha", "omega"), clients.getList("hosts", String.class));
  }

  @Test
  public void should_parse_hard_example() throws Exception {
    Toml toml = new Toml().parse(new File(getClass().getResource("hard_example.toml").getFile()));

    assertEquals("You'll hate me after this - #", toml.getString("the.test_string"));
    assertEquals(asList("] ", " # "), toml.getList("the.hard.test_array", String.class));
    assertEquals(asList("Test #11 ]proved that", "Experiment #9 was a success"), toml.getList("the.hard.test_array2", String.class));
    assertEquals(" Same thing, but with a string #", toml.getString("the.hard.another_test_string"));
    assertEquals(" And when \"'s are in the string, along with # \"", toml.getString("the.hard.harder_test_string"));
    Toml theHardBit = toml.getKeyGroup("the.hard.bit#");
    assertEquals("You don't think some user won't do that?", theHardBit.getString("what?"));
    assertEquals(asList("]"), theHardBit.getList("multi_line_array", String.class));
  }

  @SuppressWarnings("unchecked")
  private void printMap(Map<String, Object> map) {
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getValue() instanceof Map) {
        System.out.println("[" + entry.getKey() + "]");
        printMap((Map<String, Object>) entry.getValue());
        System.out.println("[/" + entry.getKey() + "]");
      } else {
        System.out.println(entry.getKey() + " = " + entry.getValue());
      }
    }
  }
}

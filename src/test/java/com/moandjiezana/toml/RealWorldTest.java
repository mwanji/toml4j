package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

public class RealWorldTest {

  @SuppressWarnings("unchecked")
  @Test
  public void should_parse_example() throws Exception {
    TomlParser parser = Parboiled.createParser(TomlParser.class);

    String toml = new Scanner(new File(getClass().getResource("example.toml").getFile())).useDelimiter("\\Z").next();
    ParsingResult<Object> result = new RecoveringParseRunner<Object>(parser.Toml()).run(toml);

    Map<String, Object> root = (Map<String, Object>) result.valueStack.peek(result.valueStack.size() - 2);

    // printMap(root);

    assertEquals("TOML Example", root.get("title"));

    Map<String, Object> owner = get(root, "owner");
    assertEquals("Tom Preston-Werner", owner.get("name"));
    assertEquals("GitHub", owner.get("organization"));
    assertEquals("GitHub Cofounder & CEO\nLikes tater tots and beer.", owner.get("bio"));

    Calendar dob = Calendar.getInstance();
    dob.set(1979, Calendar.MAY, 27, 7, 32, 0);
    dob.set(Calendar.MILLISECOND, 0);
    dob.setTimeZone(TimeZone.getTimeZone("UTC"));
    assertEquals(dob.getTime(), owner.get("dob"));

    Map<String, Object> database = get(root, "database");
    assertEquals("192.168.1.1", database.get("server"));
    assertEquals(5000L, database.get("connection_max"));
    assertTrue((Boolean) database.get("enabled"));
    assertEquals(Arrays.asList(8001L, 8001L, 8002L), database.get("ports"));

    Map<String, Object> servers = get(root, "servers");
    Map<String, Object> alphaServers = get(servers, "alpha");
    assertEquals("10.0.0.1", alphaServers.get("ip"));
    assertEquals("eqdc10", alphaServers.get("dc"));
    Map<String, Object> betaServers = get(servers, "beta");
    assertEquals("10.0.0.2", betaServers.get("ip"));
    assertEquals("eqdc10", betaServers.get("dc"));

    Map<String, Object> clients = get(root, "clients");
    assertEquals(asList(asList("gamma", "delta"), asList(1L, 2L)), clients.get("data"));
    assertEquals(asList("alpha", "omega"), clients.get("hosts"));
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

  @SuppressWarnings("unchecked")
  private Map<String, Object> get(Map<String, Object> map, String key) {
    return (Map<String, Object>) map.get(key);
  }
}

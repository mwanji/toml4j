package com.moandjiezana.toml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanTest {

  @Test
  public void should_get_boolean() throws Exception {
    Toml toml = new Toml().read("bool_false = false\nbool_true = true");

    assertFalse(toml.getBoolean("bool_false"));
    assertTrue(toml.getBoolean("bool_true"));
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_boolean_true() {
    new Toml().read("answer = true abc");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_boolean_false() {
    new Toml().read("answer = false abc");
  }
}

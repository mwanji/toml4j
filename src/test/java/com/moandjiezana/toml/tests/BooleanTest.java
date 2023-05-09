package com.moandjiezana.toml.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;

public class BooleanTest {

  @Test
  public void should_get_boolean() throws Exception {
    Toml toml = new Toml().read("bool_false = false\nbool_true = true");

    assertFalse(toml.getBoolean("bool_false"));
    assertTrue(toml.getBoolean("bool_true"));
  }

  @Test
  public void should_fail_on_invalid_boolean_true() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("answer = true abc"));
  }

  @Test
  public void should_fail_on_invalid_boolean_false() {
	  assertThrows(IllegalStateException.class, () -> new Toml().read("answer = false abc"));
  }
}

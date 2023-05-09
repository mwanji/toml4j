package com.moandjiezana.toml.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;

public class Toml_ToMapTest {
    
    @Test
    public void should_convert_simple_values() {
        Map<String, Object> toml = new Toml().read("a = 1").toMap();
        
        assertEquals(Long.valueOf(1), toml.get("a"));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void should_covert_table() throws Exception {
      Map<String, Object> toml = new Toml().read("c = 2\n  [a]\n  b = 1").toMap();
      
      assertEquals(Long.valueOf(1), ((Map<String, Object>) toml.get("a")).get("b"));
      assertEquals(Long.valueOf(2), toml.get("c"));
    }
}

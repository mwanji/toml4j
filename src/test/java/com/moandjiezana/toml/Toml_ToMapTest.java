package com.moandjiezana.toml;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class Toml_ToMapTest {
    
    @Test
    public void should_convert_simple_values() {
        Map<String, Object> toml = new Toml().read("a = 1").toMap();
        
        Assert.assertEquals(Long.valueOf(1), toml.get("a"));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void should_covert_table() throws Exception {
      Map<String, Object> toml = new Toml().read("c = 2\n  [a]\n  b = 1").toMap();
      
      Assert.assertEquals(Long.valueOf(1), ((Map<String, Object>) toml.get("a")).get("b"));
      Assert.assertEquals(Long.valueOf(2), toml.get("c"));
    }
}

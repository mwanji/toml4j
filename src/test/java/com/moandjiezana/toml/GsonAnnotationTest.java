package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.google.gson.annotations.SerializedName;
import com.moandjiezana.toml.testutils.Utils;

public class GsonAnnotationTest {

  @Test
  public void should_support_SerializedName() throws Exception {
    File file = Utils.file(getClass(), "/GsonAnnotationTest/should_support_SerializedName");
    GsonAnnotated toml = new Toml().read(file).to(GsonAnnotated.class);
    
    assertEquals("khfdaiq32-fd12-8420-2214-kafdli4328", toml.nodeId);
    assertEquals("3", toml.engineVersion);
  }
  
  private static class GsonAnnotated {

    @SerializedName("node-id")
    private String nodeId;
    @SerializedName("engine_version")
    private String engineVersion;
  }
}

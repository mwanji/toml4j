package com.moandjiezana.toml;

import java.io.File;

public class Utils {

  public static File file(Class<?> aClass, String file) {
    return new File(aClass.getResource(file + ".toml").getFile());
  }
}

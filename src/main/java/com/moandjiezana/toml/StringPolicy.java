package com.moandjiezana.toml;

public class StringPolicy {
	
  private final boolean handleMultiLineStrings;
	
  StringPolicy(boolean handleMultiLineString) {
    this.handleMultiLineStrings = handleMultiLineString;
  }
	
  boolean isHandleMultiLineStrings() {
    return this.handleMultiLineStrings;
  }

}

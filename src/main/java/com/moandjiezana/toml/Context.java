package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class Context {
  final Identifier identifier;
  final AtomicInteger line;
  
  public Context(Identifier identifier, AtomicInteger line) {
    this.identifier = identifier;
    this.line = line;
  }
}

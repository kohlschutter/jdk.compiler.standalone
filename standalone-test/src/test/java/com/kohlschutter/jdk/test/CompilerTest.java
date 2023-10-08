package com.kohlschutter.jdk.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import standalone.com.sun.tools.javac.main.JavaCompiler;

public class CompilerTest {

  @Test
  public void testCompilerVersion() throws Exception {
    assertEquals("11", JavaCompiler.version().split("\\.")[0]);
  }
}

package com.kohlschutter.jdk.standaloneutil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Provides the path for "java.home", as far as the compiler is concerned.
 */
public class JavaHomeLocator {
  private static final File COMPILER_JAVA_HOME = new File(
      "/Library/Java/JavaVirtualMachines/jdk-11.0.20.1+1/Contents/Home");

  private static final CompletableFuture<FileSystem> JRT_FS = newCompilerJrtFS();

  private static CompletableFuture<FileSystem> newCompilerJrtFS() {
    CompletableFuture<FileSystem> fs = new CompletableFuture<FileSystem>();
    try {
      fs.complete(FileSystems.newFileSystem(URI.create("jrt:/"), Collections.singletonMap(
          "java.home", getCompilerJavaHome().toString())));
    } catch (IOException e) {
      fs.completeExceptionally(e);
    }
    return fs;
  }

  public static FileSystem getCompilerJrtFS() {
    try {
      return JRT_FS.get();
    } catch (InterruptedException | ExecutionException e) {
      throw (FileSystemNotFoundException) new FileSystemNotFoundException().initCause(e);
    }
  }

  public static File getCompilerJavaHome() {
    return COMPILER_JAVA_HOME;
  }
}

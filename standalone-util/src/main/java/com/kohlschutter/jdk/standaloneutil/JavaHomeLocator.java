package com.kohlschutter.jdk.standaloneutil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Provides the path for "java.home", as far as the compiler is concerned.
 */
public class JavaHomeLocator {
  private static final Path COMPILER_JAVA_HOME = pathForResourceURL();

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

  public static Path getPathToModules() {
    return getCompilerJavaHome().resolve("modules");
  }

  public static Path getCompilerJavaHome() {
    return COMPILER_JAVA_HOME;
  }

  public static Path getCtSymPath() {
    return getCompilerJavaHome().resolve("lib").resolve("ct.sym");
  }

  private static Path pathForResourceURL() {
    try {
      return pathForResourceURL(Class.forName("com.kohlschutter.jdk.home.JavaHome"), ".");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Could not find standalone-home jar in classpath", e);
    }
  }

  private static Path pathForResourceURL(Class<?> classRef, String name) {
    boolean parentDir = ".".equals(name);

    if (parentDir || name.isEmpty()) {
      name = classRef.getSimpleName() + ".class";
    }

    URI uri;
    try {
      uri = classRef.getResource(name).toURI();
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }

    Path p;
    try {
      p = Path.of(uri);
    } catch (FileSystemNotFoundException e) {
      String ssp = uri.getSchemeSpecificPart();
      int exclp = ssp.indexOf("!/");
      if (exclp == -1) {
        throw e;
      }

      FileSystem fs;
      try {
        fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
      } catch (IOException e1) {
        throw (FileSystemNotFoundException) new FileSystemNotFoundException().initCause(e1);
      }
      p = fs.getPath(ssp.substring(exclp + 1));
    }

    if (parentDir) {
      p = p.getParent();
    }

    return p;
  }
}

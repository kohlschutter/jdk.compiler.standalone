# Standalone jdk.compiler / Java Compiler Framework + Tree API

## What

This repository builds standalone `jdk.compiler` artifacts (the Java code behind `javac`, etc.)
that can be used just like other regular Maven dependencies.

## Why

### Motivation

A typical way of using the Java Compiler API is to rely on the presence of such API in the Java VM
that runs the code requiring it.

Starting with Java 16, using the internals of the Java Compiler API requires a series of
`--add-opens` incantations to the running VM, such as:

```
--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```
etc.

The reason is clear: Eventually, we cannot rely on the presence of said API in the JDK, since
`jdk.compiler` is an internal module that we really shouldn't touch.

Since the Compiler Framework code is licensed as GPLv2+Classpath-Exception, let's make it
a separate component that we can use regardless of what's available in the
current VM!

### Benefits

With this standalone compiler, you can rely on all Java 11 javac features to be available,
even when using newer Java versions.

Specifically, this is allows you to:

- compile code even if your Java environment isn't a full JDK (Java JRE, for example!)
- target Java 1.7 for compilation without any warnings or restrictions.
- use the Compiler Tree API without resorting to `--add-opens` trickery that may eventually fail
  in newer Java releases
- build a modified compiler with additional features or custom tweaks

### Examples

See this [jsweet fork](https://github.com/kohlschutter/jsweet). jsweet makes exhaustive use of
the Compiler Tree API, and now we can run it in Java 17 without prying open some JDK internals. 

## How

### Usage

First, add the following Maven dependency to your project:

```xml
    <dependency>
        <groupId>com.kohlschutter.jdk.compiler</groupId>
        <artifactId>standalone-jdk11</artifactId>
        <version>1.0.0</version>
    </dependency>
```

If your project is modularized, also add the following statements to your `module-info.java`:

```
  requires standalone.jdk.compiler;
  requires com.kohlschutter.jdk.standaloneutil;
```

This gives you access to all `com.sun.tools.*` and `com.sun.source.*` packages, however they are
actually prefixed by `standalone.`, i.e., `standalone.com.sun.tools.*`, etc.

So you need to change your code to use `standalone.com.sun.`... instead of `com.sun.`...
For example use `standalone.com.sun.tools.javac.api.JavacTool` instead of
`com.sun.tools.javac.api.JavacTool`.

If you use `javax.tools.ToolProvider.getSystemJavaCompiler()`, change this to our own version:
`com.kohlschutter.jdk.standaloneutil.ToolProvider.getSystemJavaCompiler()` (or just modify the
`import` statement).

The original Compiler framework refers to certain files from the JDK's home directory, specifically
`lib/modules` (which contains all default `modules`), as well as `lib/ct.sym`, which contains the
API fingerprints to support `-release` compatibility checks.

The standalone compiler uses its own copies for both `lib/modules` contents as well as `lib/ct.sym`
from a recent JDK 11 java home directory, which is automatically included via the
`com.kohlschutter.jdk:standalone-home` dependency.

### Project setup and structure

The code in this repository relies on copies of the "jdk.compiler" code
obtained from Open Source Java JDKs (for example, Eclipse Temurin).

These copies reside in a separate repository and are added as submodules to this project.

Each submodule refers to a different JDK version, which allows the creation of
multiple Maven artifacts, one for each major JDK version.

### Limitations

To use this artifact, you currently need Java 11 or better.

### Building from source

Clone this repository, initialize submodules and build:

```
git clone https://github.com/kohlschutter/jdk.compiler.standalone.git
cd jdk.compiler.standalone.git
git submodule update --init
mvn clean install
```

Also see [jdk.compiler.home](https://github.com/kohlschutter/jdk.compiler.home) for the corresponding
JDK home artifact.

## When

### Future enhancements

- Next up is adding support for the compiler in Java 21.
- With a little bit of luck, we may be able to modify the compiler code enough so we can actually run
it from Java 11.
- We could even support multiple different compiler versions to run side-by-side in the same VM.
- By adding support for GraalVM native image, we could build `javac` binaries with custom
configurations
- This approach may be used for other jdk-internal components as well.

If you have an idea, please reach out!

## Who

This repository has been put together by Christian Kohlschütter.

### License

The code itself carries the original license, GNU General Public License
version 2 only, subject to the "Classpath" exception as provided in
the LICENSE file that accompanies this code.

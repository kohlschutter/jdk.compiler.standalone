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
- parse and compile Java 21 source code from Java 11 or newer
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

Also see [jdk.compiler.home](https://github.com/kohlschutter/jdk.compiler.home) for the
corresponding JDK home artifact.

### Jar with dependencies

An executable jar with all dependencies is built automatically, and placed in
`standalone-jdk11/target/standalone-jdk11-VERSION-jar-with-dependencies.jar` (for JDK11) and
`standalone-jdk21/target/standalone-jdk21-VERSION-jar-with-dependencies.jar` (for JDK21).

### GraalVM native image

Executable [GraalVM native images](https://www.graalvm.org/22.0/reference-manual/native-image/)
are built with `-Dnative` enabled (this expects `GRAALVM_HOME` to be set to the home directory of
a recent GraalVM SDK):

```
mvn clean install -Dnative
```

An executable jar with all dependencies is built automatically, and placed in
`standalone-jdk11/target/standalone-jdk11-VERSION-javac` (for JDK11) and
`standalone-jdk21/target/standalone-jdk21-VERSION-javac` (for JDK21).

### Compiling with itself

#### Using native-image
Obtain the GraalVM native-image from the step above and copy it to an external path, e.g.
`$HOME/standalone-jdk21-javac`. If you want to compile everything, you need the `jdk21` version,
otherwise `jdk11` works as well.

All you then need is to specify the path to this binary when building the project:

```
mvn clean install -Dcustom-javac=$HOME/standalone-jdk21-javac
```

#### Using jar-with-dependencies

You can also use the regular jar-with-dependencies jars, but then you need to write a little
wrapper script that can be executed by Maven:

```
#!/bin/sh

/path/to/some/java-home-directory/bin/java -jar $HOME/standalone-jdk21-jar-with-dependencies.jar $@
``` 

Save the script to an external place like `$HOME/standalone-jdk21-javac-jar-with-dependencies`
and build the project:

```
mvn clean install -Dcustom-javac=$HOME/standalone-jdk21-javac-jar-with-dependencies
```

You can experiment with using different Java JDKs/JREs to host the compiler. Anything Java 11 or
newer should work.

## When

### Future enhancements

- We could support multiple different compiler versions to run side-by-side in the same VM.
- We could build `javac` binaries (via GraalVM native-image) with custom configurations
- This approach may be used for other jdk-internal components as well.

If you have an idea, please reach out!

## When (Changelog)

### _(2023-10-13)_ jdk.compiler.home 1.1.0

- Add the compiler from JDK21, and backport it to Java 11 (!).
- Add GraalVM native-image support.
- Add self-contained jar-with-dependencies for both JDK11 and JDK21.
- Various fixes. The standalone compiler can now compile itself.

### _(2023-10-10)_ jdk.compiler.home 1.0.0

- Initial release.

## Who

This repository has been put together by [Christian Kohlschütter](https://kohlschuetter.github.io/blog/).

### Getting involved

If you encounter a bug, please file a [bug report](https://github.com/kohlschutter/jdk.compiler.standalone/issues).

If you want to contribute, please open a [pull request](https://github.com/kohlschutter/jdk.compiler.standalone/pulls)
or reach out directly.

### License

The code itself carries the original license, GNU General Public License
version 2 only, subject to the "Classpath" exception as provided in
the LICENSE file that accompanies this code.

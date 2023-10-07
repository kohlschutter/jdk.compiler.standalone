# Standalone jdk.compiler

## What

This repository builds standalone jdk.compiler artifacts that can be used just
like other regular Maven dependencies.

## Why

A typical way of using the Java Compiler API is to rely on the presence of such
API in the Java VM that runs the code requiring it.

Starting with Java 16, using the Java Compiler API requires a series of
`--add-opens` incantations to the running VM. The reason is clear: Eventually,
we cannot rely on the presence of said API in the JDK, since it is an internal
component that we really shouldn't touch.

Since the Compiler API is licensed as GPLv2+Classpath-Exception, let's make it
a separate component that we can use regardless of what's available in the
current VM.

## How

The code in this repository relies on copies of the "jdk.compiler" code
obtained from Java JDKs. That code is in a separate repository, and added as
submodules to this project.

Each submodule refers to a different JDK version, which allows the creation of
multiple Maven artifacts, one for each major JDK version.

## Who

This repository has been packaged by Christian Kohlschütter.

The code itself carries the original license, GNU General Public License
version 2 only, subject to the "Classpath" exception as provided in
the LICENSE file that accompanies this code.

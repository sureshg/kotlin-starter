---
title: 
layout: api
---



:rocket:Kotlin Starter Project

A starter template for my kotlin projects using <a href="https://github.com/gradle/gradle-script-kotlin">Gradle Script Kotlin</a>!

*

Build

``` bash
  $ ./gradlew makeExecutable -q
```

*

Generating <a href="https://github.com/gradle/gradle-script-kotlin/releases/tag/v0.8.0">AOT type-safe accessors</a> in kotlin,

``` bash
  $ ./gradlew gskGenerateAccessors
```

*

Other Tasks

``` bash
  # Run the main class.
  $ ./gradlew clean :run
  
  # Display project dependency
  $ ./gradlew dependencyInsight --dependency kotlin-stdlib  --configuration compile
  $ ./gradlew dependencies
  
  # See task tree for build task
  $ ./gradlew :build :taskTree
```

*

Another way to configure coroutine

``` kotlin
   configure<KotlinProjectExtension> {
        experimental.coroutines = ENABLE
    }
```

#### Maven Google Mirror

``` kotlin
maven { setUrl("https://maven-central.storage.googleapis.com") }
```

#### Kotlin Build Script Compilation

<a href="https://kotlinlang.slack.com/archives/gradle/p1488489798002208">Script compilation</a> happens in 4 steps:

1. Extract <code>buildscript</code> block, compile it against parent project 
   classpath (up to <code>buildSrc</code>), evaluate it against current project.
1. Extract <code>plugins</code> block,  compile it against the same classpath as 
   the previous step, evaluate it against current project.
1. MAGIC (generate Kotlin code based on information contributed by previous steps).
1. Compile whole script against classpath contributed by previous steps.

### Packages

<table class="api-docs-table">
<tbody>
<tr>
<td markdown="1">
<a href="io.sureshg/index.html">io.sureshg</a>
</td>
<td markdown="1">

</td>
</tr>
</tbody>
</table>

### Index

<a href="alltypes/index.html">All Types</a>
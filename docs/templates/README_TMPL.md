<div align="center">

# <img src="docs/logos/kotlin-icon.png" alt="Kotlin" width=25 height=25> Kotlin Starter 

 [![version][release-svg]][release-url] [![changelog][cl-svg]][cl-url] [![build][travis-svg]][travis-url] [![api-doc][apidoc-svg]][apidoc-url] [![gitter][gitter-svg]][gitter-url] [![license][license-svg]][license-url] [![kotlin][kotlin-svg]][kotlin-url] 

 A starter template for my [kotlin][kotlin-url] projects using [Gradle Kotlin DSL][kotlin-dsl]!
 
</div>

### Download

* Binary

   [![Download][release-svg]][download-url]

   > After download, make sure to set the execute permission (`chmod +x @project@`). 
   
   > Windows users can run the `executable jar` - [![Download][execjar-svg]][execjar-url].

### Build

* Source

    ```ruby
     $ git clone https://github.com/sureshg/@project@
     $ cd @project@
     $ ./gradlew makeExecutable -q
    ```
    > The binary would be located at `build/libs/@project@`
    
    Inorder to build a new version, change `appVersion` in the [gradle.properties](gradle.properties) or pass it to `./gradlew -q -PappVersion=@version@`

* API Doc

    > The API docs would be generated under the [docs](docs), which can be published as [GitHub Pages][github-pages]
    
    ```ruby
     $ cd @project@
     $ ./gradlew dokka
    ```
    
* Github Release

    > In order to publish the `@project@` binary to Github, generate [Github Access token][github-token] 
    
    ```ruby
     $ export GITHUB_TOKEN=<token>
     $ cd @project@
     $ ./gradlew prepareRelease
     $ ./gradlew githubRelease -q
    ```
    
    The latest release is [![version][release-svg]][release-url]
    
### Usage

* Help

    ```ruby
    $ @project@ --help
    ```

### Examples

* Generating [AOT type-safe accessors][kotlin-dsl-aot-doc] in kotlin, 

    ```bash
    $ ./gradlew kotlinDslAccessorsSnapshot
    ```

* Other Tasks

    ```bash
    # Run the main class.
    $ ./gradlew clean :run
    
    # Display project dependency
    $ ./gradlew dependencyInsight --dependency kotlin-stdlib  --configuration compile
    $ ./gradlew dependencies
    
    # See task tree for build task
    $ ./gradlew :build :taskTree
    ```

#### Kotlin Build Script Compilation 

[Script compilation][kotlin-slack-thread] happens in 4 steps:

 - Extract `buildscript` block, compile it against parent project 
   classpath (up to `buildSrc`), evaluate it against current project.
 - Extract `plugins` block,  compile it against the same classpath as 
   the previous step, evaluate it against current project.
 - MAGIC (generate Kotlin code based on information contributed by previous steps).
 - Compile whole script against classpath contributed by previous steps.

-----------------
<sup><b>**</b></sup>Require [Java 8 or later][java-download]

<!-- Badges -->

[apidoc-url]: https://sureshg.github.io/@project@/
[apidoc-svg]: https://img.shields.io/badge/api--doc-latest-ff69b4.svg?style=flat-square

[cl-url]: @changelogUrl@
[cl-svg]: https://img.shields.io/badge/change--log-@versionBadge@-blue.svg?style=flat-square

[release-url]: https://github.com/sureshg/@project@/releases/latest
[download-url]: https://github.com/sureshg/@project@/releases/download/@version@/@project@
[release-svg]: https://img.shields.io/github/release/sureshg/@project@.svg?style=flat-square

[execjar-url]: https://github.com/sureshg/@project@/releases/download/@version@/@project@.jar
[execjar-svg]: https://img.shields.io/badge/exec--jar-@versionBadge@-00BCD4.svg?style=flat-square

[license-url]: https://github.com/sureshg/@project@/blob/master/LICENSE
[license-svg]: https://img.shields.io/github/license/sureshg/@project@.svg?style=flat-square

[travis-url]: https://travis-ci.org/sureshg/@project@/builds
[travis-svg]: https://img.shields.io/travis/sureshg/@project@.svg?style=flat-square

[codecov-url]: https://codecov.io/gh/sureshg/@project@
[codecov-svg]: https://img.shields.io/codecov/c/github/sureshg/@project@.svg?style=flat-square

[coverall-url]: https://coveralls.io/github/sureshg/@project@?branch=master
[coverall-svg]: https://img.shields.io/coveralls/sureshg/@project@.svg?style=flat-square

[total-dl-url]: https://github.com/sureshg/@project@/releases
[total-dl-svg]: https://img.shields.io/github/downloads/sureshg/@project@/total.svg?style=flat-square

[gitter-url]: https://gitter.im/sureshg/@project@
[gitter-svg]: https://img.shields.io/gitter/room/sureshg/@project@.svg

[kotlin-url]: https://kotlinlang.org/
[kotlin-svg]: https://img.shields.io/badge/kotlin-@kotlinBadge@-green.svg?style=flat-square

[kotlin-dsl]: https://github.com/gradle/kotlin-dsl
[kotlin-dsl-aot-doc]: https://github.com/gradle/kotlin-dsl/releases/tag/v0.8.0

[kotlin-slack-thread]: https://kotlinlang.slack.com/archives/gradle/p1488489798002208
[maven-google-mirror]: https://maven-central.storage.googleapis.com
[java-download]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

[github-token]: https://github.com/settings/tokens
[github-pages]: https://pages.github.com/
[github-pages-pub]: https://help.github.com/articles/configuring-a-publishing-source-for-github-pages/


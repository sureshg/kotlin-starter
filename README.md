<div align="center">

# <img src="docs/kotlin-icon.png" alt="Kotlin" width=25 height=25> Kotlin Starter 

 [![version][release-svg]][release-url] [![changelog][cl-svg]][cl-url] [![build][travis-svg]][travis-url] [![api-doc][apidoc-svg]][apidoc-url] [![gitter][gitter-svg]][gitter-url] [![license][license-svg]][license-url] 

 A starter template for my [kotlin][kotlin] projects using [Gradle Script Kotlin][gsk]!
 
</div>

### Download

* Binary

   [![Download][release-svg]][download-url]

   > After download, make sure to set the execute permission (`chmod +x kotlin-starter`). Windows users can run the `executable jar`.

### Build

* Source

    ```ruby
     $ git clone https://github.com/sureshg/kotlin-starter
     $ cd kotlin-starter
     $ ./gradlew makeExecutable -q
    ```
    > The binary would be located at `build/libs/kotlin-starter`
    
    Inorder to build a new version, change `appVersion` in the [gradle.properties](gradle.properties) or pass it to `./gradlew -q -PappVersion=1.0.6`

* API Doc

    > The API docs would be generated under the [docs](docs), which can be published as [GitHub Pages][github-pages]
    
    ```ruby
     $ cd kotlin-starter
     $ ./gradlew dokka
    ```
    
* Github Release

    > In order to publish the `kotlin-starter` binary to Github, generate [Github Access token][github-token] 
    
    ```ruby
     $ export GITHUB_TOKEN=<token>
     $ cd kotlin-starter
     $ ./gradlew githubRelease -q
    ```
    
    The latest release is [![version][release-svg]][release-url]
    
### Usage

* Help

    ```ruby
    $ kotlin-starter --help
    ```

### Examples

* Generating [AOT type-safe accessors][gsk-aot-doc] in kotlin, 

    ```bash
    $ ./gradlew gskGenerateAccessors
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
    
* Another way to configure coroutine

    ```kotlin
     configure<KotlinProjectExtension> {
          experimental.coroutines = ENABLE
      }
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

[apidoc-url]: https://sureshg.github.io/kotlin-starter/
[apidoc-svg]: https://img.shields.io/badge/apidoc-latest-ff69b4.svg?style=flat-square

[cl-url]: https://github.com/sureshg/kotlin-starter/blob/master/CHANGELOG.md#105
[cl-svg]: https://img.shields.io/badge/changelog-1.0.6-blue.svg?style=flat-square

[release-url]: https://github.com/sureshg/kotlin-starter/releases/latest
[download-url]: https://github.com/sureshg/kotlin-starter/releases/download/1.0.6/kotlin-starter
[release-svg]: https://img.shields.io/github/release/sureshg/kotlin-starter.svg?style=flat-square

[license-url]: https://github.com/sureshg/kotlin-starter/blob/master/LICENSE
[license-svg]: https://img.shields.io/github/license/sureshg/kotlin-starter.svg?style=flat-square

[travis-url]: https://travis-ci.org/sureshg/kotlin-starter/builds
[travis-svg]: https://img.shields.io/travis/sureshg/kotlin-starter.svg?style=flat-square

[codecov-url]: https://codecov.io/gh/sureshg/kotlin-starter
[codecov-svg]: https://img.shields.io/codecov/c/github/sureshg/kotlin-starter.svg?style=flat-square

[coverall-url]: https://coveralls.io/github/sureshg/kotlin-starter?branch=master
[coverall-svg]: https://img.shields.io/coveralls/sureshg/kotlin-starter.svg?style=flat-square

[total-dl-url]: https://github.com/sureshg/kotlin-starter/releases
[total-dl-svg]: https://img.shields.io/github/downloads/sureshg/kotlin-starter/total.svg?style=flat-square

[gitter-url]: https://gitter.im/sureshg/kotlin-starter
[gitter-svg]: https://img.shields.io/gitter/room/sureshg/kotlin-starter.svg

[gsk]: https://github.com/gradle/gradle-script-kotlin
[gsk-aot-doc]: https://github.com/gradle/gradle-script-kotlin/releases/tag/v0.8.0

[kotlin]: https://kotlinlang.org/
[kotlin-slack-thread]: https://kotlinlang.slack.com/archives/gradle/p1488489798002208
[maven-google-mirror]: https://maven-central.storage.googleapis.com
[java-download]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

[github-token]: https://github.com/settings/tokens
[github-pages]: https://pages.github.com/
[github-pages-pub]: https://help.github.com/articles/configuring-a-publishing-source-for-github-pages/


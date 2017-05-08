<div align="center">
  <img src="docs/kotlin-logo.png"><br><br>
</div>

-----------------

# :rocket: Kotlin Starter Project [![version][version-svg]][download] [![api-doc][doc-svg]][apidoc-url] [![changelog][cl-svg]][cl-url]

  A starter template for my [kotlin][kotlin] projects using [Gradle Script Kotlin][gsk]!

### Download

* Binary

   [Download (v1.0.5)][download]

   > After download, make sure to set the execute permission (`chmod +x kotlin-starter`). Windows users can run the `executable jar`.

### Build

* Source

    ```ruby
     $ git clone https://github.com/sureshg/kotlin-starter
     $ cd kotlin-starter
     $ ./gradlew makeExecutable -q
    ```
    > The binary would be located at `build/libs/kotlin-starter`
    
    Inorder to build a new version, change `appVersion` in the [gradle.properties](gradle.properties) or pass it to `./gradlew -q -PappVersion=1.0.5`

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

----------
<sup>**</sup>Require [Java 8 or later][java-download]

[kotlin]: https://kotlinlang.org/
[gsk]: https://github.com/gradle/gradle-script-kotlin
[version-svg]: https://img.shields.io/badge/kotlinstarter-1.0.5-green.svg?style=flat-square
[doc-svg]: https://img.shields.io/badge/apidoc-1.0.5-ff69b4.svg?style=flat-square
[cl-svg]: https://img.shields.io/badge/changelog-1.0.5-blue.svg?style=flat-square
[cl-url]: https://github.com/sureshg/kotlin-starter/blob/master/CHANGELOG.md
[apidoc-url]: https://sureshg.github.io/kotlin-starter/
[download]: https://github.com/sureshg/kotlin-starter/releases/download/1.0.5/kotlin-starter
[github-token]: https://github.com/settings/tokens
[java-download]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[kotlin-slack-thread]: https://kotlinlang.slack.com/archives/gradle/p1488489798002208
[maven-google-mirror]: https://maven-central.storage.googleapis.com
[gsk-aot-doc]: https://github.com/gradle/gradle-script-kotlin/releases/tag/v0.8.0
[github-pages]: https://pages.github.com/
[github-pages-pub]: https://help.github.com/articles/configuring-a-publishing-source-for-github-pages/




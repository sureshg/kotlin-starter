:rocket: Kotlin Starter Project
-------------------------

A starter template for my kotlin projects using gradle-script-kotlin!

* Generating [AOT type-safe accessors](https://github.com/gradle/gradle-script-kotlin/releases/tag/v0.8.0) in kotlin, 

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
#### Maven Google Mirror

```kotlin
maven { setUrl("https://maven-central.storage.googleapis.com") }
```

#### Kotlin Build Script Compilation 

[Script compilation](https://kotlinlang.slack.com/archives/gradle/p1488489798002208) happens in 4 steps:

1. Extract `buildscript` block, compile it against parent project 
   classpath (up to `buildSrc`), evaluate it against current project.
2. Extract `plugins` block,  compile it against the same classpath as 
   the previous step, evaluate it against current project.
3. MAGIC (generate Kotlin code based on information contributed by previous steps).
4. Compile whole script against classpath contributed by previous steps.
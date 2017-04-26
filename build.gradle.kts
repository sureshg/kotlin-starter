import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import us.kirchmeier.capsule.manifest.CapsuleManifest
import us.kirchmeier.capsule.spec.ReallyExecutableSpec
import us.kirchmeier.capsule.task.*
import kotlinx.coroutines.experimental.*
import org.gradle.api.internal.HasConvention
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.script.lang.kotlin.*
import java.util.jar.Attributes

buildscript {
    var javaVersion: JavaVersion by extra
    var kotlinVersion: String by extra
    var kotlinxVersion: String by extra
    var wrapperVersion: String by extra
    var kotlinEAPRepo: String by extra
    var kotlinxRepo: String by extra
    var springBootVersion: String by extra

    javaVersion = JavaVersion.VERSION_1_8
    kotlinVersion = "1.1.2"
    kotlinxVersion = "0.14.1"
    wrapperVersion = "4.0-20170421144052+0000"
    springBootVersion = "2.0.0.BUILD-SNAPSHOT"
    kotlinxRepo = "https://dl.bintray.com/kotlin/kotlinx"
    kotlinEAPRepo = "https://dl.bintray.com/kotlin/kotlin-eap-1.1"

    repositories {
        gradleScriptKotlin()
        maven { setUrl(kotlinxRepo) }
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    }
}

val appVersion by project
val appAuthor by project
val javaVersion: JavaVersion by extra
val kotlinVersion: String by extra
val kotlinxVersion: String by extra
val springBootVersion: String by extra
val kotlinxRepo: String by extra
val kotlinEAPRepo: String by extra
val wrapperVersion: String by extra
printHeader(appVersion)

plugins {
    application
    idea
    `help-tasks`
    val pluginVersion = "1.1.1"
    id("org.jetbrains.kotlin.jvm") version pluginVersion
    id("org.jetbrains.kotlin.kapt") version pluginVersion
    id("org.jetbrains.kotlin.plugin.allopen") version pluginVersion
    id("org.jetbrains.kotlin.plugin.noarg") version pluginVersion
    id("org.jetbrains.kotlin.plugin.spring") version pluginVersion
    id("org.jetbrains.kotlin.plugin.jpa") version pluginVersion
    id("org.jetbrains.kotlin.android") version pluginVersion apply false
    id("org.jetbrains.kotlin.android.extensions") version pluginVersion apply false

    id("us.kirchmeier.capsule") version "1.0.2"
    id("com.dorongold.task-tree") version "1.3"
    id("org.springframework.boot") version "1.5.3.RELEASE"
}

/**
 * Apply third party plugins.
 */
apply {
    project.rootDir.listFiles().filter {
        it != project.buildFile && it.name.endsWith(".kts")
    }.forEach {
        from(it.name)
    }
    plugin<DependencyManagementPlugin>()
}

base {
    group = "io.sureshg"
    version = appVersion
    description = "Gradle script kotlin starter!"
}

/**
 * Configure java compiler options.
 */
java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

/**
 * Configure application plugin
 */
application {
    applicationName = rootProject.name
    mainClassName = "io.sureshg.KotlinMainKt"
}

/**
 * Enable coroutines.
 */
kotlin {
    experimental.coroutines = ENABLE
}

/**
 * Enable java incremental compilation.
 */
tasks.withType<JavaCompile> {
    options.isIncremental = true
}

/**
 * Configure kotlin compiler options.
 */
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
}

repositories {
    gradleScriptKotlin()
    maven { setUrl(kotlinEAPRepo) }
    maven { setUrl(kotlinxRepo) }
    mavenCentral()
}


val retrofitVersion = "2.2.0"
val coroutinesRetrofit = "0.5.0"
val moshiVersion = "1.4.0"
val jnrVersion = "3.0.37"
val immutableCollVersion = "0.1"
val ktSocketsVersion = "0.0.4"

dependencies {
    compile(kotlinModule("stdlib-jre8", kotlinVersion))
    compile(kotlinModule("reflect", kotlinVersion))
    compile(kotlinxModule("coroutines-core", kotlinxVersion))
    compile(kotlinxModule("collections-immutable", immutableCollVersion))
    compile(kotlinxModule("kotlin-sockets", ktSocketsVersion))
    compile("com.squareup.retrofit2:retrofit:$retrofitVersion")
    compile("com.squareup.moshi:moshi:$moshiVersion")
    compile("com.github.jnr:jnr-posix:$jnrVersion")
    compile("ru.gildor.coroutines:kotlin-coroutines-retrofit:$coroutinesRetrofit")
    compile("org.springframework.boot:spring-boot-starter")
    testCompile("org.springframework.boot:spring-boot-starter-test")
}


/**
 * Show source sets.
 */
val compileJava: JavaCompile by tasks
compileJava.doFirst {
    println("====== Source Sets ======")
    java.sourceSets.asMap.forEach { name, srcSet ->
        val ktSrcSet = (srcSet as HasConvention).convention.getPlugin<KotlinSourceSet>()
        println("Java-${name.capitalize()} => ${srcSet.allSource.srcDirs.map { it.name }}")
        println("Kotlin-${name.capitalize()} => ${ktSrcSet.kotlin.srcDirs.map { it.name }}")
    }
    println("=========================")
}

/**
 * Add jar manifests.
 */
tasks.withType<Jar> {
    manifest {
        attributes(mapOf("Built-By" to appAuthor,
                "Built-Date" to buildDateTime,
                Attributes.Name.IMPLEMENTATION_VERSION.toString() to appVersion,
                Attributes.Name.IMPLEMENTATION_TITLE.toString() to application.applicationName,
                Attributes.Name.MAIN_CLASS.toString() to application.mainClassName))
    }
}

/**
 * Auto expand gradle properties.
 */
tasks.withType<ProcessResources> {
    filesMatching("application.yaml") {
        expand(project.properties)
    }
    onlyIf { file("src/main/resources/application.yaml").exists() }
}

/**
 * Make executable
 */
task<FatCapsule>("makeExecutable") {
    val minJavaVer = javaVersion.toString()
    val appName = application.applicationName
    val appMainClass = application.mainClassName
    archiveName = appName
    reallyExecutable = ReallyExecutableSpec().regular()
    capsuleManifest = CapsuleManifest().apply {
        premainClass = "Capsule"
        mainClass = "Capsule"
        applicationName = appName
        applicationClass = appMainClass
        applicationVersion = version
        jvmArgs = listOf("-client")
        args = listOf("$*")
        minJavaVersion = minJavaVer
    }
    description = "Create $archiveName executable."
    dependsOn("clean")

    doLast {
        archivePath.setExecutable(true)
        println("Executable File: ${archivePath.absolutePath.bold}".done)
    }
}


/**
 * Generate Gradle Script Kotlin wrapper.
 */
task<Wrapper>("wrapper") {
    description = "Generate Gradle Script Kotlin wrapper v$wrapperVersion"
    distributionType = ALL
    distributionUrl = getGskURL(wrapperVersion)
    doFirst {
        println(description)
    }
}

/**
 * Task rules
 */
tasks.addRule("Pattern: extra-<PropName>: Get the project extension property value with <PropName>") {
    val taskName = this
    if (taskName.startsWith("extra-")) {
        val task = task(taskName).doLast {
            val prop = taskName.removePrefix("extra-")
            val value = project.extra.properties.getOrDefault(prop, "N/A")
            println("Extension property, $prop : $value")
        }
        task.dependsOn(tasks.getByName("clean"))
    }
}


/**
 * A tasks using coroutines.
 */
task("fib") {
    description = "A fibonacci task using kotlin generators."
    doLast {
        fib().take(10).forEach(::println)
    }
}

task("async") {
    description = "An async task using kotlin coroutines."
    doLast {
        runBlocking {
            launch(CommonPool) {
                delay(2000)
                println("Gradle Script Kotlin!")
            }
            print("Hello, ")
            delay(2000)
        }
        println("By ${appAuthor ?: "Suresh"}")
    }
}

/**
 * Set default task
 */
defaultTasks("clean", "tasks", "--all")



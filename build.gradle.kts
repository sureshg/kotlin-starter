import org.gradle.api.JavaVersion.*
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import us.kirchmeier.capsule.manifest.CapsuleManifest
import us.kirchmeier.capsule.spec.ReallyExecutableSpec
import us.kirchmeier.capsule.task.*
import kotlin.coroutines.experimental.*
import kotlinx.coroutines.experimental.*


buildscript {
    var javaVersion: JavaVersion by extra
    var kotlinVersion: String by extra
    var kotlinxVersion: String by extra
    var kotlinEAPRepo: String by extra

    kotlinVersion = "1.1.0"
    kotlinxVersion = "0.12"
    javaVersion = JavaVersion.VERSION_1_8
    kotlinEAPRepo = "http://dl.bintray.com/kotlin/kotlin-eap-1.1"

    repositories {
        gradleScriptKotlin()
        maven { setUrl(kotlinEAPRepo) }
        mavenCentral()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlinVersion))
        classpath("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    }
}

printHeader()
val javaVersion: JavaVersion by extra
val kotlinVersion: String by extra
val kotlinxVersion: String by extra
var kotlinEAPRepo: String by extra

plugins {
    java
    application
    idea
    id("us.kirchmeier.capsule") version "1.0.2"
    id("com.dorongold.task-tree") version "1.3"
}

/**
 * Apply third party plugins.
 */
apply {
    plugin("kotlin")
}

base {
    group = "io.sureshg"
    version = "1.0"
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
val app = application {
    applicationName = "kotlin-starter"
    mainClassName = "io.sureshg.KotlinMainKt"
}

/**
 * Configure kotlin compiler options.
 */
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
}

/**
 * Enable coroutines.
 */
configure<KotlinProjectExtension> {
    experimental.coroutines = ENABLE
}

repositories {
    gradleScriptKotlin()
    maven { setUrl(kotlinEAPRepo) }
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jre8", kotlinVersion))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
}

/**
 * Auto expand gradle properties.
 */
tasks.withType<ProcessResources> {
    filesMatching("application.yaml") {
        expand(project.properties)
    }
    onlyIf { false }
}

/**
 * Make executable
 */
task<FatCapsule>("makeExecutable") {
    // val appConfig = project.convention.getPlugin(ApplicationPluginConvention::class)
    val minJavaVer = javaVersion.toString()
    archiveName = app.applicationName
    reallyExecutable = ReallyExecutableSpec().trampolining()
    capsuleManifest = CapsuleManifest().apply {
        premainClass = "Capsule"
        mainClass = "Capsule"
        applicationName = app.applicationName
        applicationClass = app.mainClassName
        applicationVersion = version
        jvmArgs = listOf("-client")
        args = listOf("$*")
        minJavaVersion = minJavaVer
    }
}

/**
 * Generate Gradle Script Kotlin wrapper.
 */
task<Wrapper>("wrapper") {
    //gradleVersion = "3.5"
    distributionType = ALL
    distributionUrl = getGskURL("3.5-20170305000422+0000")
}

/**
 * A tasks using coroutines.
 */
task("fib") {
    doLast {
        fib().take(10).forEach(::println)
    }
}

task("async") {
    doLast {
        runBlocking {
            launch(CommonPool) {
                delay(2000)
                println("Gradle Script Kotlin!")
            }
            print("Hello, ")
            delay(2000)
        }
    }
}

/**
 * Helper/extension functions.
 */
fun getGskURL(version: String, type: DistributionType = ALL) = "https://repo.gradle.org/gradle/dist-snapshots/gradle-script-kotlin-$version-${type.name.toLowerCase()}.zip"

fun fib() = buildSequence {
    var a = 0
    var b = 1
    while (true) {
        yield(b)
        val next = a + b
        a = b
        b = next
    }
}

fun printHeader() {
    val header = """
                 +-----------------------------+
                 | Kotlin Starter Build Script |
                 +-----------------------------+
                 """.trimIndent()
    println(header)
    println("\nConfigured project properties are,")
    extra.properties.entries.sortedBy { it.key }.forEach {
        println("%-18s = %-20s".format(it.key, it.value))
    }
    println()
}



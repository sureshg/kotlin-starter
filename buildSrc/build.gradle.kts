import org.gradle.api.tasks.Delete
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

buildscript {
    var kotlinVersion: String by extra
    var kotlinxVersion: String by extra
    var kotlinEAPRepo: String by extra

    kotlinVersion = "1.1.2"
    kotlinxVersion = "0.14.1"
    kotlinEAPRepo = "https://dl.bintray.com/kotlin/kotlin-eap-1.1"

    repositories {
        gradleScriptKotlin()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.1.1"
}

val kotlinVersion: String by extra
val kotlinxVersion: String by extra
val kotlinEAPRepo: String by extra

/**
 * Cleaning buildSrc before compilation.
 */
afterEvaluate {
    val clean: Delete by tasks
    val compileKotlin: KotlinCompile by tasks
    compileKotlin.dependsOn(clean)
}

/**
 * Enable coroutines.
 */
kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

repositories {
    gradleScriptKotlin()
    maven { setUrl(kotlinEAPRepo) }
}

dependencies {
    compile(gradleScriptKotlinApi())
    compile(kotlinModule("stdlib-jre8", kotlinVersion))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
}

/**
 * Retrieves the [kotlin][KotlinProjectExtension] project extension.
 */
val Project.kotlin: KotlinProjectExtension get() = extensions.getByName("kotlin") as KotlinProjectExtension

/**
 * Configures the [kotlin][KotlinProjectExtension] project extension.
 */
fun Project.kotlin(configure: KotlinProjectExtension.() -> Unit): Unit = extensions.configure("kotlin", configure)
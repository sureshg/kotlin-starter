import org.jetbrains.kotlin.gradle.dsl.*

buildscript {
    repositories {
        gradleScriptKotlin()
    }
}

plugins {
    val kotlinVersion = System.getProperty("kotlin.version") ?: "1.1.2-2"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
}

configure<KotlinProjectExtension> {
    experimental.coroutines = Coroutines.ENABLE
}

val kotlinxVersion = System.getProperty("kotlinx.version")
val kotlinRepo = System.getProperty("kotlin.eap.repo")

repositories {
    gradleScriptKotlin()
    maven { setUrl(kotlinRepo) }
}

/**
 * BuildSrc dependencies.
 */
dependencies {
    compile(gradleScriptKotlinApi())
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
}

tasks.getByName("compileKotlin").dependsOn("clean")

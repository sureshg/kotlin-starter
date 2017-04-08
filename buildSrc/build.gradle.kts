buildscript {

    var kotlinVersion: String by extra
    var kotlinEAPRepo: String by extra

    kotlinVersion = "1.1.2-eap-44"
    kotlinEAPRepo = "https://dl.bintray.com/kotlin/kotlin-eap-1.1"

    repositories {
        gradleScriptKotlin()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.1.1"
}

val kotlinVersion: String by extra
val kotlinEAPRepo: String by extra

repositories {
    gradleScriptKotlin()
    maven { setUrl(kotlinEAPRepo) }
}

dependencies {
    // compile(gradleScriptKotlinApi())
    compile(kotlinModule("stdlib-jre8", kotlinVersion))
}
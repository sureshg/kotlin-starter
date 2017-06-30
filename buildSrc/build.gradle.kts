import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    `kotlin-dsl`
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

repositories {
    jcenter()
}

dependencies {
    compile(gradleKotlinDsl())
    compile(kotlin("stdlib-jre8"))
}

tasks.getByName("compileKotlin").dependsOn("clean")

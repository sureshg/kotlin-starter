import co.riiid.gradle.ReleaseTask
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
import org.jetbrains.dokka.gradle.*
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
    kotlinVersion = "1.1.2-2"
    kotlinxVersion = "0.14.1"
    wrapperVersion = "4.0-20170427155501+0000"
    springBootVersion = "1.5.3.RELEASE"
    kotlinxRepo = "https://dl.bintray.com/kotlin/kotlinx"
    kotlinEAPRepo = "https://dl.bintray.com/kotlin/kotlin-eap-1.1"

    repositories {
        gradleScriptKotlin()
        maven { setUrl(kotlinxRepo) }
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.13")
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
    val ktPlugin = "1.1.2-2"
    val dokkaPlugin = "0.9.13"
    val bootPlugin = "1.5.3.RELEASE"

    application
    idea
    `help-tasks`
    id("org.jetbrains.kotlin.jvm") version ktPlugin
    id("org.jetbrains.kotlin.kapt") version ktPlugin
    id("org.jetbrains.kotlin.plugin.allopen") version ktPlugin
    id("org.jetbrains.kotlin.plugin.noarg") version ktPlugin
    id("org.jetbrains.kotlin.plugin.spring") version ktPlugin
    id("org.jetbrains.kotlin.plugin.jpa") version ktPlugin
    id("org.jetbrains.kotlin.android") version ktPlugin apply false
    id("org.jetbrains.kotlin.android.extensions") version ktPlugin apply false
    id("us.kirchmeier.capsule") version "1.0.2"
    id("com.dorongold.task-tree") version "1.3"
    id("co.riiid.gradle") version "0.4.2"
    id("org.springframework.boot") version bootPlugin
    //id("org.jetbrains.dokka") version dokkaPlugin
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

    plugin<DokkaPlugin>()
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
    mainClassName = "$group.MainKt"
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

dependencies {
    val retrofitVersion = "2.2.0"
    val coroutinesRetrofit = "0.5.0"
    val moshiVersion = "1.4.0"
    val jnrVersion = "3.0.37"
    val immutableCollVersion = "0.1"
    val ktSocketsVersion = "0.0.4"

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
 * Generate doc using dokka.
 */
tasks.withType<DokkaTask> {
    val src = "src/main/kotlin"
    val out = "$projectDir/docs"
    val format = DokkaFormat.KotlinWeb
    doFirst {
        println("Cleaning ${out.bold} directory...".cyan)
        project.delete(fileTree(out) {
            exclude("kotlin-*.png")
        })
    }

    moduleName = ""
    outputFormat = format.type
    outputDirectory = out
    jdkVersion = javaVersion.majorVersion.toInt()
    includes = listOf("README.md")
    val mapping = LinkMapping().apply {
        dir = src
        url = "https://github.com/sureshg/kotlin-starter/blob/master/$src"
        suffix = "#L"
    }
    linkMappings = arrayListOf(mapping)
    description = "Generate docs in ${format.desc} format."

    doLast {
        println("Generated ${format.desc} format docs to ${outputDirectory.bold}".done)
    }
}

/**
 * Github release config and set token.
 */
github {
    baseUrl = "https://api.github.com"
    owner = "sureshg"
    repo = application.applicationName
    tagName = version.toString()
    targetCommitish = "master"
    name = "${application.applicationName} v$version"
    val changelog = "${baseUrl.replace(".api", "", true)}/$owner/$repo/blob/$targetCommitish/CHANGELOG.md"
    body = "$name release. Check [CHANGELOG.md]($changelog) for details."
    setAssets(File(buildDir, "libs/${application.applicationName}").path)
}

tasks.withType<ReleaseTask> {
    doFirst {
        github.token = getEnv("GITHUB_TOKEN")
    }

    doLast {
        println("Published github release ${github.name}.".done)
        println("Release URL: ${githubReleaseURL(owner = github.owner, repo = github.repo).bold}")
    }

    description = "Publish Github release ${github.name}"
    dependsOn("makeExecutable")
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




import term.*
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


buildscript {
    var javaVersion: JavaVersion by extra
    var kotlinVersion: String by extra
    var kotlinxVersion: String by extra
    var wrapperVersion: String by extra
    var kotlinEAPRepo: String by extra
    var kotlinxRepo: String by extra
    var dokkaVersion: String by extra
    var springBootVersion: String by extra

    javaVersion = JavaVersion.VERSION_1_8
    kotlinVersion = "kotlin.version".sysProp
    kotlinxVersion = "kotlinx.version".sysProp
    wrapperVersion = "wrapper.version".sysProp
    kotlinEAPRepo = "kotlin.eap.repo".sysProp
    kotlinxRepo = "kotlinx.repo".sysProp
    dokkaVersion = "dokka.version".sysProp
    springBootVersion = "springboot.version".sysProp

    repositories {
        gradleScriptKotlin()
        maven { setUrl(kotlinxRepo) }
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
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
    val ktPlugin = "kotlin.version".sysProp
    val dokkaPlugin = "dokka.version".sysProp
    val bootPlugin = "springboot.version".sysProp

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
    // id("org.jetbrains.dokka") version dokkaPlugin
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
    mainClassName = "${project.group}.MainKt"
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
        attributes(mapOf(
                BuildInfo.Author.attr to appAuthor,
                BuildInfo.Date.attr to buildDateTime,
                BuildInfo.JDK.attr to "java.version".sysProp,
                BuildInfo.Target.attr to javaVersion,
                BuildInfo.OS.attr to "${"os.name".sysProp} ${"os.version".sysProp}",
                BuildInfo.KotlinVersion.attr to kotlinVersion,
                BuildInfo.CreatedBy.attr to "Gradle ${gradle.gradleVersion}",
                BuildInfo.AppVersion.attr to appVersion,
                BuildInfo.Title.attr to application.applicationName,
                BuildInfo.Vendor.attr to project.group,
                BuildInfo.MainClass.attr to application.mainClassName))
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
        val size = archivePath.length().toBinaryPrefixString(si = true)
        println("Executable File: ${archivePath.absolutePath.bold} (${size.bold})".done)
    }
}


/**
 * Generate doc using dokka.
 */
tasks.withType<DokkaTask> {
    val src = "src/main"
    val out = "$projectDir/docs"
    val format = DokkaFormat.Html
    doFirst {
        println("Cleaning doc directory ${out.bold}...".cyan)
        project.delete(fileTree(out) {
            exclude("kotlin-*.png")
        })
    }

    moduleName = ""
    sourceDirs = files(src)
    outputFormat = format.type
    outputDirectory = out
    jdkVersion = javaVersion.majorVersion.toInt()
    includes = listOf("README.md", "CHANGELOG.md")
    val mapping = LinkMapping().apply {
        dir = src
        url = "${githubRepo.url}/blob/master/$src"
        suffix = "#L"
    }
    linkMappings = arrayListOf(mapping)
    description = "Generate docs in ${format.desc} format."

    doLast {
        println("Generated ${format.desc} format docs to ${outputDirectory.bold}".done)
    }
}

/**
 * Set Github token and publish.
 */
github {
    val tag = version.toString()
    baseUrl = "https://api.github.com"
    owner = githubRepo.user
    repo = githubRepo.repo
    tagName = tag
    targetCommitish = "master"
    name = "${application.applicationName.capitalize()} v$version"
    val changelog = githubRepo.changelogUrl(branch = targetCommitish, tag = tag)
    body = ":mega: $name release. Check [CHANGELOG.md]($changelog) for details. :tada:"
    setAssets(File(buildDir, "libs/${application.applicationName}").path)
}

tasks.withType<ReleaseTask> {
    doFirst {
        github.token = getEnv("GITHUB_TOKEN")
    }

    doLast {
        println("Published github release ${github.name}.".done)
        println("Release URL: ${githubRepo.releaseUrl().bold}")
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




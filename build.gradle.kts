import term.*
import BuildInfo.*
import plugins.*
import org.gradle.kotlin.dsl.*
import org.gradle.jvm.tasks.Jar
import co.riiid.gradle.ReleaseTask
import kotlinx.coroutines.experimental.*
import us.kirchmeier.capsule.task.*
import us.kirchmeier.capsule.manifest.CapsuleManifest
import us.kirchmeier.capsule.spec.ReallyExecutableSpec
import org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.internal.HasConvention
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.junit.platform.gradle.plugin.JUnitPlatformPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.jetbrains.dokka.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper

buildscript {
    repositories {
        jcenter()
        maven { setUrl(kotlinEapRepoSysProp) }
    }
    dependencies {
        // Require only for EAPs
        listOf("kotlin-gradle-plugin", "kotlin-allopen", "kotlin-noarg").forEach {
            classpath("org.jetbrains.kotlin:$it:$kotlinSysProp")
        }
        classpath("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxSysProp")
        classpath("org.junit.platform:junit-platform-gradle-plugin:$junitSysProp")
    }
}

val appVersion by project
val appAuthor by project
val javaVersion: JavaVersion by extra { JavaVersion.VERSION_1_8 }
val kotlinVersion: String by extra { kotlinSysProp }
val kotlinxVersion: String by extra { kotlinxSysProp }
val wrapperVersion: String by extra { wrapperSysProp }
val kotlinEAPRepo: String by extra { kotlinEapRepoSysProp }
val kotlinxRepo: String by extra { kotlinxRepoSysProp }
val springBootVersion: String by extra { springBootSysProp }
printHeader(appVersion)

plugins {
    id("com.gradle.build-scan") version buildScanSysProp
    application
    java
    idea
    maven
    jacoco
    `help-tasks`
    // For stable versions,
    // kotlin(it, kotlinSysProp)
    id("org.springframework.boot") version springBootSysProp
    id("com.github.johnrengelman.shadow") version shadowSysProp
    id("com.github.ben-manes.versions") version versionsSysProp
    id("org.jlleitschuh.gradle.ktlint") version ktlintSysProp
    id("org.jetbrains.dokka") version dokkaSysProp
    id("us.kirchmeier.capsule") version "1.0.2"
    id("com.dorongold.task-tree") version "1.3"
    id("co.riiid.gradle") version "0.4.2"
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
    kotlinPlugins.forEach {
        plugin("org.jetbrains.kotlin.$it")
    }
    plugin<BookPlugin>()
    plugin<JUnitPlatformPlugin>()
    plugin<DependencyManagementPlugin>()
}

group = "io.sureshg"
version = appVersion
description = "Gradle Kotlin DSL starter!"

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
 * Enable build scan
 */
buildScan {
    setLicenseAgreementUrl("https://gradle.com/terms-of-service")
    setLicenseAgree("yes")
    tag(appVersion.toString())
    link("GitHub", githubRepo.url)
    buildFinished {
        value("Build Result", failure?.message ?: "")
    }
}

/**
 * Custom plugin.
 */
books.invoke {
    "quickStart" {
        sourceFile = file("quick-start.pdf")
    }
    "userGuide" {

    }
    "developerGuide" {

    }
}


/**
 * A Configuration represents a group of artifacts and their dependencies.
 */
configurations {
    "testConfig" {
        configurations.compile.extendsFrom(this)
        isTransitive = false
    }
}


/**
 * Maven pom config. Can use [GenerateMavenPom] if you are
 * using maven-publish plugin .
 */
maven {
    mvnpom {
        withXml {
            val deps = asNode().appendNode("dependencies")
            configurations.compile.dependencies.forEach {
                with(deps.appendNode("dependency")) {
                    appendNode("groupId", it.group)
                    appendNode("artifactId", it.name)
                    appendNode("version", it.version)
                }
            }
        }
    }
}

task("generatePom") {
    doLast {
        println("Generating the Maven POM file.".fg256())
        maven.pom().writeTo("build/resources/main/META-INF/maven/${project.group}/${project.name}/pom.xml")
    }
    tasks.getByName("jar").dependsOn(this)
    description = "Generates the Maven POM file for ${project.name} v$appVersion"
}

/**
 * Java code coverage metrics.
 */
tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
        csv.isEnabled = false
    }
    val jacocoTestReport by tasks
    jacocoTestReport.dependsOn("test")
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
        // javaParameters = true
    }
}

/**
 * Dependency version check. The current resolution strategy
 * would disallow (only if "DisallowRC" env variable is set)
 * release candidates as upgradable versions.
 */
tasks.withType<DependencyUpdatesTask> {
    revision = "milestone"
    outputFormatter = "plain"
    description = "Displays the $revision dependency updates for ${project.name} v$appVersion"
    if (System.getenv("DisallowRC").toBoolean()) {
        resolutionStrategy = closureOf<ComponentSelectionRules> {
            all { selection: ComponentSelection ->
                val rcs = listOf("alpha", "beta", "rc", "cr", "m")
                val rejected = rcs.any {
                    selection.candidate.version.matches("/(?i).*[.-]$it[.\\d-]*/".toRegex())
                }
                if (rejected) selection.reject("Release candidate.")
            }
        }
    }
}

repositories {
    maven { setUrl(kotlinEAPRepo) }
    maven { setUrl(kotlinxRepo) }
    jcenter()
    mavenCentral()
}

/**
 * Configures subproject's buildscript repo .A `buildscript` block is ignored
 * unless it’s at the top level, the [ScriptHandler] instance available via
 * the `buildscript` property however, can always be configured.
 */
subprojects {
    buildscript.repositories {
        jcenter()
        mavenCentral()
    }
}

dependencies {
    compile(kotlin("stdlib-jre8", kotlinVersion))
    compile(kotlin("reflect", kotlinVersion))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    compile("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.1")
    compile("com.squareup.retrofit2:retrofit:2.3.0")
    compile("net.jodah:failsafe:1.0.4")
    compile("com.squareup.moshi:moshi:1.5.0")
    compile("com.github.jnr:jnr-posix:3.0.41")
    compile("ru.gildor.coroutines:kotlin-coroutines-retrofit:0.5.1")
    compile("org.springframework.boot:spring-boot-starter")
    // compile("net.bytebuddy:byte-buddy:1.7.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("org.mockito:mockito-core:2.8.47")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.0.0-M4")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.0.0-M4")
    "testConfig"("com.google.code.findbugs:jsr305:3.0.2")
}

/**
 * Kotlin Plugin version.
 */
val Project.kotlinPluginVersion get() = plugins.filterIsInstance<KotlinBasePluginWrapper>().firstOrNull()?.kotlinPluginVersion

/**
 * Source set configuration
 */
val SourceSet.kotlin: SourceDirectorySet get() = (this as HasConvention).convention.getPlugin<KotlinSourceSet>().kotlin

tasks.withType<JavaCompile> {
    doFirst {
        sourceSets {
            main {
                println("${name.capitalize()} => Java : ${java.srcDirs}, Kotlin: ${kotlin.srcDirs}, Resource: ${resources.srcDirs}".dot.fg256())
            }
            test {
                println("${name.capitalize()} => Java : ${java.srcDirs}, Kotlin: ${kotlin.srcDirs}, Resource: ${resources.srcDirs}".dot.fg256())
            }
        }
    }
}

/**
 * Add jar manifests.
 */
tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                Author.attr to appAuthor,
                Date.attr to buildDateTime,
                JDK.attr to sysProp("java.version"),
                BuildTarget.attr to javaVersion,
                OS.attr to "${sysProp("os.name")} ${sysProp("os.version")}",
                KotlinVersion.attr to kotlinVersion,
                CreatedBy.attr to "Gradle ${gradle.gradleVersion}",
                AppVersion.attr to appVersion,
                Title.attr to application.applicationName,
                Vendor.attr to project.group,
                MainClass.attr to application.mainClassName))
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
val capsuleTask = task<FatCapsule>("makeExecutable") {
    val minJavaVer = javaVersion.toString()
    val appName = application.applicationName
    val appMainClass = application.mainClassName
    archiveName = appName
    reallyExecutable(closureOf<ReallyExecutableSpec> { regular() })
    capsuleManifest(closureOf<CapsuleManifest> {
        premainClass = "Capsule"
        mainClass = "Capsule"
        applicationName = appName
        applicationClass = appMainClass
        applicationVersion = version
        jvmArgs = listOf("-client", "-Djava.security.egd=file:/dev/./urandom")
        args = listOf("$*")
        minJavaVersion = minJavaVer
    })
    description = "Create $archiveName executable."
    dependsOn("clean", "shadowJar")

    doLast {
        archivePath.setExecutable(true)
        val size = archivePath.length().toBinaryPrefixString()
        println("Executable File: ${archivePath.absolutePath.bold} (${size.bold})".done)
    }
}

/**
 * Creates fat-jar/uber-jar.
 */
val shadowTasks = tasks.withType<ShadowJar> {
    classifier = ""
    version = ""
    description = "Create a fat JAR of ${project.name} v$appVersion and runtime dependencies."
    doLast {
        val size = archivePath.length().toBinaryPrefixString()
        println("FatJar: ${archivePath.path.bold} (${size.bold})".done)
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
            exclude("logos/**", "templates/**")
        })
    }

    moduleName = ""
    sourceDirs = files(src)
    outputFormat = format.type
    outputDirectory = out
    skipEmptyPackages = true
    jdkVersion = javaVersion.majorVersion.toInt()
    includes = listOf("README.md", "CHANGELOG.md")
    val mapping = LinkMapping().apply {
        dir = src
        url = "${githubRepo.url}/blob/master/$src"
        suffix = "#L"
    }
    linkMappings = arrayListOf(mapping)
    description = "Generate ${project.name} v$appVersion docs in ${format.desc} format."

    doLast {
        println("Generated ${format.desc} format docs to ${outputDirectory.bold}".done)
    }
}

/**
 * Generate ReadMe for the project.
 */
task<Copy>("generateReadMe") {
    val version = appVersion.toString()
    val tokens = mapOf("version" to version,
            "versionBadge" to version.replace("-", "--"),
            "kotlin" to kotlinVersion,
            "kotlinBadge" to kotlinVersion.replace("-", "--"),
            "changelogUrl" to githubRepo.changelogUrl(tag = version),
            "project" to project.name)
    inputs.properties(tokens)
    from("docs/templates")
    into(projectDir)
    rename("_TMPL", "")
    include("*.md")
    filter<ReplaceTokens>("tokens" to tokens)
    filteringCharset = "UTF-8"
    description = "Generate README.md for ${application.applicationName.capitalize()} v$version release."
}

/**
 * Prepare the docs and release version.
 */
task("prepareRelease") {
    description = "Prepare ${application.applicationName.capitalize()} v$version release."
    doLast {
        println("Make sure to commit the doc changes before doing ${"./gradlew githubRelease".bold}".dot.cyan)
    }
    dependsOn("generateReadMe", "dokka")
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
    setAssets(capsuleTask.archivePath.path, shadowTasks.first().archivePath?.path)
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
                println("Gradle Kotlin DSL!")
            }
            print("Hello, ")
            delay(2000)
        }
        println("By ${appAuthor ?: "Suresh"}")
    }
}


/**
 * Generate Gradle Kotlin DSL wrapper.
 */
task<Wrapper>("wrapper") {
    description = "Generate Gradle Kotlin DSL wrapper ${wrapperVersion.bold}"
    distributionType = Wrapper.DistributionType.ALL
    distributionUrl = gradleKotlinDslUrl(wrapperVersion)
    doFirst {
        println(description)
    }
}

/**
 * Set default task
 */
defaultTasks("clean", "tasks", "--all")



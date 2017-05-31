import term.*
import BuildInfo.*
import org.gradle.jvm.tasks.Jar
import co.riiid.gradle.ReleaseTask
import org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import us.kirchmeier.capsule.manifest.CapsuleManifest
import us.kirchmeier.capsule.spec.ReallyExecutableSpec
import us.kirchmeier.capsule.task.*
import kotlinx.coroutines.experimental.*
import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.jetbrains.dokka.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import com.github.benmanes.gradle.versions.updates.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens


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
    val shadowPlugin = "shadow.version".sysProp
    val buildScan = "build-scan.version".sysProp
    val ktlintVersion = "ktlint.version".sysProp
    val gradleVersion = "gradle-versions.version".sysProp

    id("com.gradle.build-scan") version buildScan
    application
    java
    idea
    maven
    jacoco
    `help-tasks`
    id("org.jetbrains.kotlin.jvm") version ktPlugin
    id("org.jetbrains.kotlin.kapt") version ktPlugin
    id("org.jetbrains.kotlin.plugin.allopen") version ktPlugin
    id("org.jetbrains.kotlin.plugin.noarg") version ktPlugin
    id("org.jetbrains.kotlin.plugin.spring") version ktPlugin
    id("org.jetbrains.kotlin.plugin.jpa") version ktPlugin
    id("org.springframework.boot") version bootPlugin
    id("com.github.johnrengelman.shadow") version shadowPlugin
    id("com.github.ben-manes.versions") version gradleVersion
    id("us.kirchmeier.capsule") version "1.0.2"
    id("com.dorongold.task-tree") version "1.3"
    id("co.riiid.gradle") version "0.4.2"
    // id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
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
    gradleScriptKotlin()
    maven { setUrl(kotlinEAPRepo) }
    maven { setUrl(kotlinxRepo) }
    mavenCentral()
}

/**
 * Configures subproject's buildscript repo .A `buildscript` block is ignored
 * unless it’s at the top level, the [ScriptHandler] instance available via
 * the `buildscript` property however, can always be configured.
 */
subprojects {
    buildscript.repositories {
        mavenCentral()
    }
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
    compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    compile("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.1")
    compile("org.jetbrains.kotlinx:kotlin-sockets:0.0.10")
    compile("com.squareup.retrofit2:retrofit:2.3.0")
    compile("net.jodah:failsafe:1.0.4")
    compile("com.squareup.moshi:moshi:1.5.0")
    compile("com.github.jnr:jnr-posix:3.0.41")
    compile("ru.gildor.coroutines:kotlin-coroutines-retrofit:0.5.0")
    compile("org.springframework.boot:spring-boot-starter")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    testCompile("org.springframework.boot:spring-boot-starter-test")
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
                JDK.attr to "java.version".sysProp,
                BuildTarget.attr to javaVersion,
                OS.attr to "${"os.name".sysProp} ${"os.version".sysProp}",
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
    reallyExecutable = ReallyExecutableSpec().regular()
    capsuleManifest = CapsuleManifest().apply {
        premainClass = "Capsule"
        mainClass = "Capsule"
        applicationName = appName
        applicationClass = appMainClass
        applicationVersion = version
        jvmArgs = listOf("-client", "-Djava.security.egd=file:/dev/./urandom")
        args = listOf("$*")
        minJavaVersion = minJavaVer
    }
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
 * Generate Gradle Script Kotlin wrapper.
 */
task<Wrapper>("wrapper") {
    description = "Generate Gradle Script Kotlin wrapper v$wrapperVersion"
    distributionType = Wrapper.DistributionType.ALL
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

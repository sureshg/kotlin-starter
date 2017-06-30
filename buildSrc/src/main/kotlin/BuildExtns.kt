import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenPom
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.MavenPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import org.gradle.kotlin.dsl.embeddedKotlinVersion
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the
import term.bold
import term.cyan
import term.fg256
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.experimental.buildSequence

/**
 * Kotlin build script extension functions.
 *
 * @author Suresh
 */

/**
 * Returns the system property value of given string.
 */
fun sysProp(name: String, default: String = ""): String = System.getProperty(name, default)

val GradleSnapShotURL = sysProp("gradle.snap.url")
val kotlinSysProp = sysProp("kotlin.version")
val kotlinxSysProp = sysProp("kotlinx.version")
val dokkaSysProp = sysProp("dokka.version")
val springBootSysProp = sysProp("springboot.version")
val shadowSysProp = sysProp("shadow.version")
val buildScanSysProp = sysProp("build-scan.version")
val ktlintSysProp = sysProp("ktlint.version")
val versionsSysProp = sysProp("gradle-versions.version")
val wrapperSysProp = sysProp("wrapper.version")
val junitSysProp = sysProp("junit-plugin.version")
val kotlinEapRepoSysProp = sysProp("kotlin.eap.repo")
val kotlinxRepoSysProp = sysProp("kotlinx.repo")

/**
 * Visit the [plugin portal](https://plugins.gradle.org/search?term=org.jetbrains.kotlin) to see the list of available plugins.
 */
val kotlinPlugins = listOf("jvm", "kapt", "plugin.allopen", "plugin.noarg", "plugin.spring", "plugin.jpa")

fun gradleKotlinDslUrl(version: String, type: DistributionType = DistributionType.ALL) = "$GradleSnapShotURL/$version-${type.name.toLowerCase()}.zip"

val buildDateTime by lazy { ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z")) }

/**
 * Returns [true] if it's running on IntelliJ IDEs.
 */
val isIdea = sysProp("idea.executable") == "idea"

/**
 * Really Executable Jar header.
 *
 * @see https://goo.gl/Y8VvR7
 */
val EXEC_JAR_HEADER = """|#!/bin/sh
                         |
                         |exec java -jar "$0" "$@"
                         |
                      """.trimMargin()

/**
 * Returns [GithubRepo] config for the current project.
 *
 * The regex basically supports the following kinds of Github repo urls,
 *  - git://github.com/some-user/my-repo.git/
 *  - git@github.com:some-user/my-repo.git
 *  - https://github.com/some-user/my-repo.git
 *  - https://github.com/some-user/my-repo
 *
 * @see The regex representation - https://goo.gl/f95P06
 */
val githubRepo by lazy {
    val repoUrlRegex = "(git|ssh|https?)(?:@|://)([\\w.-]+)[:/]([\\w.-]+)/([\\w-]+)(?:\\.git)?".toRegex()
    val repoUrl = sysProp("github.repo.url")
    repoUrlRegex.find(repoUrl, 0)?.groups?.let {
        GithubRepo(proto = it[1]!!.value,
                baseUrl = it[2]!!.value,
                user = it[3]!!.value,
                repo = it[4]!!.value)
    } ?: throw IllegalArgumentException("Invalid Github repo url: $repoUrl")
}

/**
 * Prints the project header.
 */
fun Project.printHeader(version: Any?, embdKtVersion: String = embeddedKotlinVersion) {
    val header = """|======================
                           |Kotlin Starter v$version
                           |======================
                         """.trimMargin()
    println(header.bold.cyan)
    println("\nEmbedded kotlin version: $embdKtVersion".fg256())
    println("Configured project properties are,")
    extra.properties.entries.sortedBy { it.key }.forEach {
        println("%-18s = %-20s".format(it.key, it.value).fg256())
    }
    println()
}

/**
 * Maven pom config convenient extensions.
 */
fun MavenPluginConvention.mvnpom(config: MavenPom.() -> Unit) = pom().apply(config)

/**
 * Java/Kotlin source set extensions.
 *
 * @see https://goo.gl/1FR6qw
 */
fun Project.sourceSets(block: SourceSetContainer.() -> Unit) = the<JavaPluginConvention>().sourceSets.apply(block)

/**
 * Main and test source config extensions.
 */
val SourceSetContainer.main: SourceSet get() = this["main"]
val SourceSetContainer.test: SourceSet get() = this["test"]
fun SourceSetContainer.main(block: SourceSet.() -> Unit) = main.apply(block)
fun SourceSetContainer.test(block: SourceSet.() -> Unit) = test.apply(block)


/**
 * Gets the value of the specified environment variable. If it's not set (null),
 * it will prompt the user to enter value on the system console. For password
 * input masking, run gradle with "--no-deamon" option.
 *
 * @param envVar system environment variable.
 * @param mask [true] if the env value echoing is disabled on console.
 */
fun Project.getEnv(envVar: String, mask: Boolean = true): String {
    var env = System.getenv(envVar)
    if (env == null) {
        val con = System.console()
        val msg = "> Please enter ${envVar.bold}: "
        env = when (con) {
        // daemon mode.
            null -> {
                println(msg)
                readLine() ?: ""
            }
        //--no-daemon
            else -> when (mask) {
                true -> String(con.readPassword(msg) ?: "".toCharArray())
                else -> con.readLine(msg) ?: ""
            }
        }
    }
    return env
}

/**
 * Extension method to traverse the task graph.
 */
fun Project.showTaskGraph() {
    println("└── Task graph".cyan)
    gradle.taskGraph.whenReady {
        allTasks.forEach {
            println("    └── $it".fg256())
        }
    }
}

/**
 * Async helper function.
 */
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

/**
 * Represents a  Github repository.
 */
data class GithubRepo(val proto: String,
                      val baseUrl: String,
                      val user: String,
                      val repo: String,
                      val branch: String = "master",
                      val url: String = "https://$baseUrl/$user/$repo") {

    /**
     * Change log date format.
     */
    companion object {
        val clDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    /**
     * Returns the github release url for the specific [tag].
     */
    fun releaseUrl(tag: String = "latest") = when (tag) {
        "latest" -> "$url/releases/latest"
        else -> "$url/releases/tag/$tag"
    }

    /**
     * Returns README.md url for the [branch].
     */
    fun readmeUrl(branch: String = this.branch) = "$url/blob/$branch/README.md"

    /**
     *  Returns CHANGELOG.md url for the specified [branch] and [tag].
     *  Set [keepAChangeLog] to [true] for http://keepachangelog.com/ style.
     */
    fun changelogUrl(branch: String = this.branch, tag: String = "", keepAChangeLog: Boolean = true): String {
        val suffix = when {
            tag.isEmpty() -> ""
            else -> {
                val anchor = "#${tag.replace(".", "")}"
                if (keepAChangeLog) "$anchor---$clDate" else anchor
            }
        }
        return "$url/blob/$branch/CHANGELOG.md$suffix"
    }
}

/**
 * Dokka output format.
 */
enum class DokkaFormat(val type: String, val desc: String) {
    Html("html", "HTML Doc"),
    HtmlAsJava("html-as-java", "Html using java syntax"),
    KotlinWeb("kotlin-website", "Kotlin Website"),
    Markdown("markdown", "Markdown(md) doc"),
    Gfm("gfm", "GitHub-Flavored Markdown"),
    Jekyll("jekyll", "Markdown adapted for Jekyll sites"),
    JavaDoc("javadoc", "Javadoc format")
}
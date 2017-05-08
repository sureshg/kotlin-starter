import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.script.lang.kotlin.creating
import org.gradle.script.lang.kotlin.embeddedKotlinVersion
import org.gradle.script.lang.kotlin.extra
import term.bold
import term.cyan
import term.fg256
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.experimental.buildSequence

/**
 * Kotlin build script extension functions.
 *
 * @author Suresh
 */
const val GradleSnapShotURL = "https://repo.gradle.org/gradle/dist-snapshots"

fun getGskURL(version: String, type: org.gradle.api.tasks.wrapper.Wrapper.DistributionType = org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL) = "$GradleSnapShotURL/gradle-script-kotlin-$version-${type.name.toLowerCase()}.zip"

val buildDateTime by lazy { ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z")) }

fun DependencyHandler.kotlinxModule(module: String, version: String) = "org.jetbrains.kotlinx:${if (module.startsWith("kotlin", true)) "" else "kotlinx-"}$module:$version"

/**
 * Extension function to create new task.
 */
inline fun <reified T : Task> Project.task(noinline config: T.() -> Unit) = tasks.creating(T::class, config)

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
 * Returns the github release url for the specific repo and tag.
 */
fun Project.githubReleaseURL(baseUrl: String = "https://github.com", owner: String, repo: String, tag: String? = null) = when (tag == null) {
    true -> "$baseUrl/$owner/$repo/releases/latest"
    else -> "$baseUrl/$owner/$repo/releases/tag/$tag"
}

/**
 * Extension method to traverse the task graph.
 */
fun Project.showTaskGraph() {
    println("└── Task graph".cyan)
    gradle.taskGraph.whenReady {
        it.allTasks.forEach {
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
 * Dokka output format.
 */
enum class DokkaFormat(val type: String, val desc: String) {
    Html("html", "HTML Doc"),
    KotlinWeb("kotlin-website", "Kotlin Website"),
    Markdown("markdown", "Markdown(md) doc"),
    Gfm("gfm", "GitHub-Flavored Markdown"),
    Jekyll("jekyll", "Markdown adapted for Jekyll sites"),
    JavaDoc("javadoc", "Javadoc format")
}
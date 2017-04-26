import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.script.lang.kotlin.creating
import org.gradle.script.lang.kotlin.embeddedKotlinVersion
import org.gradle.script.lang.kotlin.extra
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.experimental.buildSequence

/**
 * Kotlin build script extension functions..
 *
 * @author Suresh
 */
fun getGskURL(version: String, type: Wrapper.DistributionType = Wrapper.DistributionType.ALL) = "https://repo.gradle.org/gradle/dist-snapshots/gradle-script-kotlin-$version-${type.name.toLowerCase()}.zip"

val buildDateTime by lazy { ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z")) }

fun DependencyHandler.kotlinxModule(module: String, version: String) = "org.jetbrains.kotlinx:${if (module.startsWith("kotlin", true)) "" else "kotlinx-"}$module:$version"

/**
 * Extension function to create new task.
 */
inline fun <reified T : Task> Project.task(noinline config: T.() -> Unit) = tasks.creating(T::class, config)

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
 * Dokka output format.
 */
enum class DokkaFormat(val desc: String) {
    HTML("HTML Doc"),
    MarkDown("Markdown(md) doc"),
    GFM("GitHub-Flavored Markdown"),
    JEKYLL("Markdown adapted for Jekyll sites"),
    JAVADOC("Javadoc format")
}
package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.*
import term.fg256
import java.io.File

/**
 * Custom book Plugin
 */
open class BookPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.run {
            val books = container(Book::class.java) { name ->
                Book(name, file("src/docs/$name"))
            }
            extensions.add("books", books)

            tasks {
                val hello by creating {
                    group = "misc"
                    doLast { println("Hello Book Plugin!") }
                }

                "allBooks" {
                    group = "misc"
                    dependsOn(hello)
                    doLast {
                        println("Task $name for project ${project.name}")
                        books.forEach { println(it.toString().fg256()) }
                    }
                }

                "appRun"(Exec::class) {
                    group = "misc"
                    setCommandLine("pwd")
                }
            }
        }
    }
}

data class Book(val name: String, var sourceFile: File)
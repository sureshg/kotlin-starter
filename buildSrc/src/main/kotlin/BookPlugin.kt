import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * Custom book Plugin
 */
class BookPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val books = project.container(Book::class.java)
        project.extensions.add("books", books)
    }
}

data class Book(val name: String, var sourceFile: File)
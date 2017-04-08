import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Common build tasks.
 */
open class MyExecTask : DefaultTask() {

    @Input
    var command = listOf("ls")

    override fun getDescription() = "MyExecTask for ${project.name}"

    @TaskAction
    fun run() {
        project.exec {
            setWorkingDir(project.buildDir)
            setCommandLine(command)
        }
    }
}

task<MyExecTask>("exec-task") {
    command = listOf("ls", "-ltrha")
}

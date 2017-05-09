package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * An exec task.
 */
open class MyExecTask : DefaultTask() {
    @Input var command = listOf("ls")

    override fun getDescription() = "MyExecTask for ${project.name}"

    @TaskAction fun run() {
        project.exec {
            it.workingDir = project.buildDir
            it.commandLine = command
        }
    }
}
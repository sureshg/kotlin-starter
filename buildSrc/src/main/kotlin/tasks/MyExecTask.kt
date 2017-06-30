package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.*

/**
 * A custom exec task.
 */
open class MyExecTask : DefaultTask() {

    init {
        group = "misc"
        description = "Custom Exec Task for ${project.name}"
    }

    @Input var command = listOf("ls")

    @TaskAction fun run() {
        println("Executing $command for ${this.project.name}.")
        project.exec {
            workingDir = project.buildDir
            commandLine = command
        }
    }
}

fun Project.myExecTask() = task<MyExecTask>("myExecTask")
import org.apache.tools.ant.taskdefs.ExecTask
import org.gradle.api.tasks.Exec
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

open class TT : Exec() {
    @TaskAction
   fun run() {

    }
}

task<TT> ("ee") {
  setCommandLine("pwd")
}

task<MyExecTask>("yoo") {
    command = listOf("ls", "-latrh")
    project.exec {
        setCommandLine("pwd")
    }
}

task<Exec>("xxxx") {
    setCommandLine(listOf("ls", "-latrh"))
}

/**
 *
 */
tasks {

 val tt: org.gradle.api.tasks.Copy by tasks

}
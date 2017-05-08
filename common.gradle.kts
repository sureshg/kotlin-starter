/**
 * Some common tasks script.
 */

task<MyExecTask>("exec-task") {
    command = listOf("ls", "-ltarh")
}

/**
 * Task rules
 */
tasks.addRule("Pattern: extra-<PropName>: Get the project extension property value with <PropName>") {
    val taskName = this
    if (taskName.startsWith("extra-")) {
        val task = task(taskName).doLast {
            val prop = taskName.removePrefix("extra-")
            val value = project.extra.properties.getOrDefault(prop, "N/A")
            println("Extension property, $prop : $value".cyan)
        }
        task.dependsOn(tasks.getByName("clean"))
    }
}
package io.sureshg

/**
 * Idiomatic kotlin
 *
 * @author Suresh (@sur3shg)
 */
fun main(args: Array<String>) {
    //explosivePlaceHolder()
    semValidation("HelloKotlin")
    println(join("|", listOf("Kotlin", "is", "awesome!")))
}

/**
 * Kotlin explosive place holder.
 */
fun explosivePlaceHolder(): String = TODO("Will do later!")

fun semValidation(msg: String) {
    requireNotNull(msg) { "Message can't be null" }
    require(msg.length > 5) { "$msg length should be > 5" }

    check(msg.length > 3) { "$msg size should be > 3" }
    checkNotNull(msg) { "Message can't be null" }

    assert(msg.substring(2).equals("kotlin", true)) { "$msg - not a valid message." }
}

data class User(val name: String)

fun anyOrNothing(user: User?) {
    val name = user?.name ?: throw IllegalStateException("User was null")
    println("Name is $name")
}

@Deprecated("String strings.joinToString(sep).", ReplaceWith("strings.joinToString(separator = sep)"), level = DeprecationLevel.WARNING)
fun join(sep: String, strings: List<String>) = strings.joinToString(separator = sep)

sealed class UiOp(val name: String) {
    object show : UiOp("show")
    object hide : UiOp("hide")
    data class Custom(val type: String) : UiOp(type)
}



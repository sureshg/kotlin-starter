package io.sureshg

import java.util.*

/**
 * Idiomatic kotlin
 *
 * @author Suresh (@sur3shg)
 */


fun getDefaultLocate(area: String) = when (area.toLowerCase()) {
    "germany", "austria" -> Locale.GERMAN
    "france" -> Locale.FRENCH
    "china" -> Locale.CHINESE
    else -> Locale.ENGLISH
}

fun main(args: Array<String>) {
    //explosivePlaceHolder()
    semValidation("HelloKotlin")
}

fun explosivePlaceHolder(): String = TODO("Will do later!")

fun semValidation(msg: String) {
    requireNotNull(msg) { "Message can't be null" }
    require(msg.length > 5) { "$msg length should be > 5" }

    check(msg.length > 3) { "$msg size should be > 3" }
    checkNotNull(msg) { "Message can't be null" }

    assert(msg.substring(2).equals("kotlin", true)) { "$msg - not a valid message." }

    val (text,url) = Item("","")
}

sealed class UiOp(val ss: String) {

    object show: UiOp("")
    object hide: UiOp("")
    data class Test(val a : String) : UiOp(a)

}

data class Item(val text: String, val url: String)


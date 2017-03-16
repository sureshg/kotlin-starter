package io.sureshg

import kotlinx.collections.immutable.*
import kotlinx.coroutines.experimental.*

/**
 * Kotlin class.
 *
 * @author Suresh G (@sur3shg)
 */

fun main(args: Array<String>) {
    runBlocking {
        launch(CommonPool) {
            delay(1000)
            println("Kotlin!")
        }
        print("Hello, ")
        delay(1000)
    }

    val list1 = immutableListOf("Kotlin", "Java", "Scala", "Clojure", "Ceylon")
    val list2 = list1.mutate { it.add("Groovy") }
    val list3 = list2 - "Groovy"
    println("Immutable list1 : $list1 , Hashcode: ${list1.hashCode()}")
    println("Immutable list2 : $list2 , Hashcode: ${list2.hashCode()}")
    println("Immutable list3 : $list3 , Hashcode: ${list3.hashCode()}")
}
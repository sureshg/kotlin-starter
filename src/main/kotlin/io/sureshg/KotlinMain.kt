package io.sureshg

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
}
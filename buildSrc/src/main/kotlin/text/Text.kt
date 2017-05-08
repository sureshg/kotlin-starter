package text

import kotlin.coroutines.experimental.buildSequence

/**
 * These extension are copied from https://github.com/xenomachina/xenocom.
 */
const val NBSP_CODEPOINT = 0xa0

/**
 * Produces a [Sequence] of the Unicode code points in the given [String].
 */
fun String.codePointSequence(): Sequence<Int> = buildSequence {
    val length = length
    var offset = 0
    while (offset < length) {
        val codePoint = codePointAt(offset)
        yield(codePoint)
        offset += Character.charCount(codePoint)
    }
}

/**
 * Clear [StringBuilder]
 */
fun StringBuilder.clear() {
    this.setLength(0)
}

/**
 * Trims newline at the end.
 */
fun String.trimNewline(): String {
    if (endsWith('\n')) {
        return substring(0, length - 1)
    } else {
        return this
    }
}

package io.sureshg.extn

import kotlin.coroutines.experimental.buildSequence

/**
 * These extension are copied from https://github.com/xenomachina/xenocom.
 */
const val NBSP_CODEPOINT = 0xa0

internal const val SPACE_WIDTH = 1

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

fun StringBuilder.clear() {
    this.setLength(0)
}

fun String.trimNewline(): String {
    if (endsWith('\n')) {
        return substring(0, length - 1)
    } else {
        return this
    }
}

fun String.padLinesToWidth(width: Int): String {
    val sb = StringBuilder()
    var lineWidth = 0
    var singleLine = true
    for (codePoint in codePointSequence()) {
        if (codePoint == '\n'.toInt()) {
            singleLine = false
            while (lineWidth < width) {
                sb.append(" ")
                lineWidth += SPACE_WIDTH
            }
            sb.append("\n")
            lineWidth = 0
        } else {
            sb.appendCodePoint(codePoint)
            lineWidth += codePointWidth(codePoint)
        }
    }
    if (singleLine || lineWidth > 0) {
        while (lineWidth < width) {
            sb.append(" ")
            lineWidth += SPACE_WIDTH
        }
    }
    return sb.toString()
}

fun String.wrapText(maxWidth: Int): String {
    val sb = StringBuilder()
    val word = StringBuilder()
    var lineWidth = 0
    var wordWidth = 0
    fun handleSpace() {
        if (wordWidth > 0) {
            if (lineWidth > 0) {
                sb.append(" ")
                lineWidth += SPACE_WIDTH
            }
            sb.append(word)
            lineWidth += wordWidth
            word.clear()
            wordWidth = 0
        }
    }
    for (inputCodePoint in codePointSequence()) {
        if (Character.isSpaceChar(inputCodePoint) && inputCodePoint != NBSP_CODEPOINT) {
            // space
            handleSpace()
        } else {
            // non-space
            val outputCodePoint = if (inputCodePoint == NBSP_CODEPOINT) ' '.toInt() else inputCodePoint
            val charWidth = codePointWidth(outputCodePoint).toInt()
            if (lineWidth > 0 && lineWidth + SPACE_WIDTH + wordWidth + charWidth > maxWidth) {
                sb.append("\n")
                lineWidth = 0
            }
            if (lineWidth == 0 && lineWidth + SPACE_WIDTH + wordWidth + charWidth > maxWidth) {
                // Eep! Word would be longer than line. Need to break it.
                sb.append(word)
                word.clear()
                wordWidth = 0
                sb.append("\n")
                lineWidth = 0
            }
            word.appendCodePoint(outputCodePoint)
            wordWidth += charWidth
        }
    }
    handleSpace()

    return sb.toString()
}

/**
 * Returns an estimated cell width of a Unicode code point when displayed on a monospace terminal.
 * Possible return values are -1, 0, 1 or 2. Control characters (other than null) and Del return -1.
 *
 * This function is based on the public domain [wcwidth.c](https://www.cl.cam.ac.uk/~mgk25/ucs/wcwidth.c)
 * written by Markus Kuhn.
 */
fun codePointWidth(ucs: Int): Int {
    // 8-bit control characters
    if (ucs == 0) return 0
    if (ucs < 32 || (ucs >= 0x7f && ucs < 0xa0)) return -1

    // Non-spacing characters. This is simulating the binary search of
    // `uniset +cat=Me +cat=Mn +cat=Cf -00AD +1160-11FF +200B`.
    if (ucs != 0x00AD) { // soft hyphen
        val category = Character.getType(ucs).toByte()
        if (category == Character.ENCLOSING_MARK || // "Me"
                category == Character.NON_SPACING_MARK || // "Mn"
                category == Character.FORMAT || // "Cf"
                (ucs in 0x1160..0x11FF) || // Hangul Jungseong & Jongseong
                ucs == 0x200B) // zero width space
            return 0
    }

    // If we arrive here, ucs is not a combining or C0/C1 control character.
    return if (ucs >= 0x1100 && (ucs <= 0x115f || // Hangul Jamo init. consonants
            ucs == 0x2329 || ucs == 0x232a ||
            (ucs in 0x2e80..0xa4cf && ucs != 0x303f) || // CJK ... Yi
            (ucs in 0xac00..0xd7a3) || // Hangul Syllables
            (ucs in 0xf900..0xfaff) || // CJK Compatibility Ideographs
            (ucs in 0xfe10..0xfe19) || // Vertical forms
            (ucs in 0xfe30..0xfe6f) || // CJK Compatibility Forms
            (ucs in 0xff00..0xff60) || // Fullwidth Forms
            (ucs in 0xffe0..0xffe6) ||
            (ucs in 0x20000..0x2fffd) ||
            (ucs in 0x30000..0x3fffd)))
        2 else 1
}

fun String.codePointWidth(): Int = codePointSequence().sumBy { codePointWidth(it) }

fun columnize(vararg s: String, minWidths: IntArray? = null): String {
    val columns = Array(s.size) { mutableListOf<String>() }
    val widths = IntArray(s.size)
    for (i in 0..s.size - 1) {
        if (minWidths != null && i < minWidths.size) {
            widths[i] = minWidths[i]
        }
        for (line in s[i].lineSequence()) {
            val cell = line.trimNewline()
            columns[i].add(cell)
            widths[i] = maxOf(widths[i], cell.codePointWidth())
        }
    }
    val height = columns.maxBy { it.size }?.size ?: 0
    val sb = StringBuilder()
    var firstLine = true
    for (j in 0..height - 1) {
        if (firstLine) {
            firstLine = false
        } else {
            sb.append("\n")
        }
        var lineWidth = 0
        var columnStart = 0
        for (i in 0..columns.size - 1) {
            columns[i].getOrNull(j)?.let { cell ->
                for (k in 1..columnStart - lineWidth) sb.append(" ")
                lineWidth = columnStart
                sb.append(cell)
                lineWidth += cell.codePointWidth()
            }
            columnStart += widths[i]
        }
    }
    return sb.toString()
}
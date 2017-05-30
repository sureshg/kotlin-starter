package io.sureshg.extn

/**
 * Glyph object provides Unicode glyphs as well as it's plain ASCII alternatives.
 * All ASCII glyphs are guaranteed to be the same number of characters as the
 * corresponding Unicode glyphs, so that they line up properly when printed on
 * a terminal. The orginal data file is taken from "Dart term_glyph" project.
 *
 * @author Suresh
 *
 * @see https://github.com/dart-lang/term_glyph
 */
object Glyph {

    /**
     * Whether the glyph return plain ASCII, as opposed to Unicode characters.
     * Defaults to [false].
     */
    var ascii = false

    /**
     * 1. A bullet point.
     */
    val bullet get() = if (ascii) "*" else "•"


    /**
     * 2. A left-pointing arrow.
     */
    val leftArrow get() = if (ascii) "<" else "←"


    /**
     * 3. A right-pointing arrow.
     */
    val rightArrow get() = if (ascii) ">" else "→"


    /**
     * 4. An upwards-pointing arrow.
     */
    val upArrow get() = if (ascii) "^" else "↑"


    /**
     * 5. A downwards-pointing arrow.
     */
    val downArrow get() = if (ascii) "v" else "↓"


    /**
     * 6. A two-character left-pointing arrow.
     */
    val longLeftArrow get() = if (ascii) "<=" else "◀━"


    /**
     * 7. A two-character right-pointing arrow.
     */
    val longRightArrow get() = if (ascii) "=>" else "━▶"


    /**
     * 8. A horizontal line that can be used to draw a box.
     */
    val horizontalLine get() = if (ascii) "-" else "─"


    /**
     * 9. A vertical line that can be used to draw a box.
     */
    val verticalLine get() = if (ascii) "|" else "│"


    /**
     * 10. The upper left-hand corner of a box.
     */
    val topLeftCorner get() = if (ascii) "," else "┌"


    /**
     * 11. The upper right-hand corner of a box.
     */
    val topRightCorner get() = if (ascii) "," else "┐"


    /**
     * 12. The lower left-hand corner of a box.
     */
    val bottomLeftCorner get() = if (ascii) "'" else "└"


    /**
     * 13. The lower right-hand corner of a box.
     */
    val bottomRightCorner get() = if (ascii) "'" else "┘"


    /**
     * 14. An intersection of vertical and horizontal box lines.
     */
    val cross get() = if (ascii) "+" else "┼"


    /**
     * 15. A horizontal box line with a vertical line going up from the middle.
     */
    val teeUp get() = if (ascii) "+" else "┴"


    /**
     * 16. A horizontal box line with a vertical line going down from the middle.
     */
    val teeDown get() = if (ascii) "+" else "┬"


    /**
     * 17. A vertical box line with a horizontal line going left from the middle.
     */
    val teeLeft get() = if (ascii) "+" else "┤"


    /**
     * 18. A vertical box line with a horizontal line going right from the middle.
     */
    val teeRight get() = if (ascii) "+" else "├"


    /**
     * 19. The top half of a vertical box line.
     */
    val upEnd get() = if (ascii) "'" else "╵"


    /**
     * 20. The bottom half of a vertical box line.
     */
    val downEnd get() = if (ascii) "," else "╷"


    /**
     * 21. The left half of a horizontal box line.
     */
    val leftEnd get() = if (ascii) "-" else "╴"


    /**
     * 22. The right half of a horizontal box line.
     */
    val rightEnd get() = if (ascii) "-" else "╶"


    /**
     * 23. A bold horizontal line that can be used to draw a box.
     */
    val horizontalLineBold get() = if (ascii) "=" else "━"


    /**
     * 24. A bold vertical line that can be used to draw a box.
     */
    val verticalLineBold get() = if (ascii) "|" else "┃"


    /**
     * 25. The bold upper left-hand corner of a box.
     */
    val topLeftCornerBold get() = if (ascii) "," else "┏"


    /**
     * 26. The bold upper right-hand corner of a box.
     */
    val topRightCornerBold get() = if (ascii) "," else "┓"


    /**
     * 27. The bold lower left-hand corner of a box.
     */
    val bottomLeftCornerBold get() = if (ascii) "'" else "┗"


    /**
     * 28. The bold lower right-hand corner of a box.
     */
    val bottomRightCornerBold get() = if (ascii) "'" else "┛"


    /**
     * 29. An intersection of bold vertical and horizontal box lines.
     */
    val crossBold get() = if (ascii) "+" else "╋"


    /**
     * 30. A bold horizontal box line with a vertical line going up from the middle.
     */
    val teeUpBold get() = if (ascii) "+" else "┻"


    /**
     * 31. A bold horizontal box line with a vertical line going down from the middle.
     */
    val teeDownBold get() = if (ascii) "+" else "┳"


    /**
     * 32. A bold vertical box line with a horizontal line going left from the middle.
     */
    val teeLeftBold get() = if (ascii) "+" else "┫"


    /**
     * 33. A bold vertical box line with a horizontal line going right from the middle.
     */
    val teeRightBold get() = if (ascii) "+" else "┣"


    /**
     * 34. The top half of a bold vertical box line.
     */
    val upEndBold get() = if (ascii) "'" else "╹"


    /**
     * 35. The bottom half of a bold vertical box line.
     */
    val downEndBold get() = if (ascii) "," else "╻"


    /**
     * 36. The left half of a bold horizontal box line.
     */
    val leftEndBold get() = if (ascii) "-" else "╸"


    /**
     * 37. The right half of a bold horizontal box line.
     */
    val rightEndBold get() = if (ascii) "-" else "╺"


    /**
     * 38. A double horizontal line that can be used to draw a box.
     */
    val horizontalLineDouble get() = if (ascii) "=" else "═"


    /**
     * 39. A double vertical line that can be used to draw a box.
     */
    val verticalLineDouble get() = if (ascii) "|" else "║"


    /**
     * 40. The double upper left-hand corner of a box.
     */
    val topLeftCornerDouble get() = if (ascii) "," else "╔"


    /**
     * 41. The double upper right-hand corner of a box.
     */
    val topRightCornerDouble get() = if (ascii) "," else "╗"


    /**
     * 42. The double lower left-hand corner of a box.
     */
    val bottomLeftCornerDouble get() = if (ascii) "\"" else "╚"


    /**
     * 43. The double lower right-hand corner of a box.
     */
    val bottomRightCornerDouble get() = if (ascii) "\"" else "╝"


    /**
     * 44. An intersection of double vertical and horizontal box lines.
     */
    val crossDouble get() = if (ascii) "+" else "╬"


    /**
     * 45. A double horizontal box line with a vertical line going up from the middle.
     */
    val teeUpDouble get() = if (ascii) "+" else "╩"


    /**
     * 46. A double horizontal box line with a vertical line going down from the middle.
     */
    val teeDownDouble get() = if (ascii) "+" else "╦"


    /**
     * 47. A double vertical box line with a horizontal line going left from the middle.
     */
    val teeLeftDouble get() = if (ascii) "+" else "╣"


    /**
     * 48. A double vertical box line with a horizontal line going right from the middle.
     */
    val teeRightDouble get() = if (ascii) "+" else "╠"


    /**
     * 49. A dashed horizontal line that can be used to draw a box.
     */
    val horizontalLineDoubleDash get() = if (ascii) "-" else "╌"


    /**
     * 50. A bold dashed horizontal line that can be used to draw a box.
     */
    val horizontalLineDoubleDashBold get() = if (ascii) "-" else "╍"


    /**
     * 51. A dashed vertical line that can be used to draw a box.
     */
    val verticalLineDoubleDash get() = if (ascii) "|" else "╎"


    /**
     * 52. A bold dashed vertical line that can be used to draw a box.
     */
    val verticalLineDoubleDashBold get() = if (ascii) "|" else "╏"


    /**
     * 53. A dashed horizontal line that can be used to draw a box.
     */
    val horizontalLineTripleDash get() = if (ascii) "-" else "┄"


    /**
     * 54. A bold dashed horizontal line that can be used to draw a box.
     */
    val horizontalLineTripleDashBold get() = if (ascii) "-" else "┅"


    /**
     * 55. A dashed vertical line that can be used to draw a box.
     */
    val verticalLineTripleDash get() = if (ascii) "|" else "┆"


    /**
     * 56. A bold dashed vertical line that can be used to draw a box.
     */
    val verticalLineTripleDashBold get() = if (ascii) "|" else "┇"


    /**
     * 57. A dashed horizontal line that can be used to draw a box.
     */
    val horizontalLineQuadrupleDash get() = if (ascii) "-" else "┈"


    /**
     * 58. A bold dashed horizontal line that can be used to draw a box.
     */
    val horizontalLineQuadrupleDashBold get() = if (ascii) "-" else "┉"


    /**
     * 59. A dashed vertical line that can be used to draw a box.
     */
    val verticalLineQuadrupleDash get() = if (ascii) "|" else "┊"


    /**
     * 60. A bold dashed vertical line that can be used to draw a box.
     */
    val verticalLineQuadrupleDashBold get() = if (ascii) "|" else "┋"

}
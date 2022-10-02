package pl.bfelis.fc93.language.util

import pl.bfelis.fc93.language.ast.Statement
import pl.bfelis.fc93.language.parser.Parser
import pl.bfelis.fc93.language.scanner.Scanner

fun String.parse(): List<Statement?> {
    val scanner = Scanner(this)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    return parser.parse()
}

package pl.bfelis.llang.language.util

import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.parser.Parser
import pl.bfelis.llang.language.scanner.Scanner

fun String.parse(): List<Statement?> {
    val scanner = Scanner(this)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    return parser.parse()
}

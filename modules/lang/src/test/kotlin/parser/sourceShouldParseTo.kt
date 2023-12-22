package parser

import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.parser.Parser
import pl.bfelis.llang.language.scanner.Scanner
import kotlin.test.assertEquals

fun sourceShouldParseTo(source: String, statements: List<Statement?>) {
    val scanner = Scanner(source)
    val parser = Parser(scanner.scanTokens(), null)

    assertEquals(statements, parser.parse())
}

fun sourceShouldParseTo(source: String) {
    val scanner = Scanner(source)
    val parser = Parser(scanner.scanTokens(), null)

    println(parser.parse())
}

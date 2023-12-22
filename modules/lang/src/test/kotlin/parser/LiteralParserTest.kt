package parser

import io.kotest.core.spec.style.ShouldSpec
import pl.bfelis.llang.language.ast.Expr
import pl.bfelis.llang.language.ast.Statement

class LiteralParserTest : ShouldSpec({
    context("number") {
        should("should return literal expression with number parsed") {
            sourceShouldParseTo("123", listOf(Statement.Expression(Expr.Literal(123.0))))
        }
    }
    context("string") {
        should("should return literal expression with string parsed") {
            sourceShouldParseTo("\"abc\"", listOf(Statement.Expression(Expr.Literal("abc"))))
        }
    }
})

package parser

import io.kotest.core.spec.style.ShouldSpec
import pl.bfelis.llang.language.ast.Expr
import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.scanner.Token
import pl.bfelis.llang.language.scanner.TokenType

class VariableParserTest : ShouldSpec({
    context("define var") {
        should("should return variable expression with literal assignment") {
            sourceShouldParseTo("var v = 123", listOf(Statement.Var(Token(TokenType.IDENTIFIER, "v", null, 1), Expr.Literal(123.0))))
        }
    }
    context("define val") {
        should("should return val expression with literal assignment") {
            sourceShouldParseTo("val v = 123", listOf(Statement.Val(Token(TokenType.IDENTIFIER, "v", null, 1), Expr.Literal(123.0))))
        }
    }
})

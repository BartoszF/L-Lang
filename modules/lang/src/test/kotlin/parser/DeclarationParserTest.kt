package parser

import io.kotest.core.spec.style.ShouldSpec
import pl.bfelis.llang.language.ast.Expr
import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.scanner.Token
import pl.bfelis.llang.language.scanner.TokenType

class DeclarationParserTest : ShouldSpec({
    context("define var") {
        should("return variable expression with literal assignment") {
            sourceShouldParseTo(
                "var v = 123",
                listOf(Statement.Var(Token(TokenType.IDENTIFIER, "v", null, 1), Expr.Literal(123.0)))
            )
        }
    }
    context("define val") {
        should("return val expression with literal assignment") {
            sourceShouldParseTo(
                "val v = 123",
                listOf(Statement.Val(Token(TokenType.IDENTIFIER, "v", null, 1), Expr.Literal(123.0)))
            )
        }
    }

    context("define function") {
        should("should return empty function") {
            sourceShouldParseTo(
                "fun test() { }",
                listOf(
                    Statement.Function(
                        Token(TokenType.IDENTIFIER, "test", null, 1),
                        params = emptyList(),
                        body = emptyList(),
                        isStatic = false
                    )
                )
            )
        }

        should("should return empty function with param") {
            sourceShouldParseTo(
                "fun test(a) { }",
                listOf(
                    Statement.Function(
                        Token(TokenType.IDENTIFIER, "test", null, 1),
                        params = listOf(Token(TokenType.IDENTIFIER, "a", null, 1)),
                        body = emptyList(),
                        isStatic = false
                    )
                )
            )
        }
    }
})

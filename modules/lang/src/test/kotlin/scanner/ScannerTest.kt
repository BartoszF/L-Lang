package scanner

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.bfelis.llang.language.scanner.Scanner
import pl.bfelis.llang.language.scanner.TokenType

private fun sourceShouldScanTo(source: String, tokenType: TokenType, lexeme: String? = null, literal: Any? = null) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()
    println(tokens)
    tokens.size shouldBe 2
    tokens[0].type shouldBe tokenType
    if (lexeme != null) {
        tokens[0].lexeme shouldBe lexeme
    }
    if (literal != null) {
        tokens[0].literal shouldBe literal
    }
    tokens[1].type shouldBe TokenType.EOF
}

internal class ScannerTest : ShouldSpec({

    context("number") {
        should("should return number and EOF") {
            sourceShouldScanTo("123", TokenType.NUMBER, literal = 123)
        }
    }

    context("string") {
        should("should return string and EOF") {
            sourceShouldScanTo("\"ABC\"", TokenType.STRING, literal = "ABC")
        }
    }

    context("identifier") {
        should("should return identifier and EOF") {
            sourceShouldScanTo("ABC", TokenType.IDENTIFIER, lexeme = "ABC")
        }
    }

    context("identifiers") {
        should("should return VAR and EOF") {
            sourceShouldScanTo("var", TokenType.VAR)
        }
        should("should return VAL and EOF") {
            sourceShouldScanTo("val", TokenType.VAL)
        }
        should("should return FUN and EOF") {
            sourceShouldScanTo("fun", TokenType.FUN)
        }

        should("should return AND and EOF") {
            sourceShouldScanTo("and", TokenType.AND)
        }
        should("should return CLASS and EOF") {
            sourceShouldScanTo("class", TokenType.CLASS)
        }
        should("should return ELSE and EOF") {
            sourceShouldScanTo("else", TokenType.ELSE)
        }
        should("should return FALSE and EOF") {
            sourceShouldScanTo("false", TokenType.FALSE)
        }
        should("should return FOR and EOF") {
            sourceShouldScanTo("for", TokenType.FOR)
        }
        should("should return IF and EOF") {
            sourceShouldScanTo("if", TokenType.IF)
        }
        should("should return NIL and EOF") {
            sourceShouldScanTo("nil", TokenType.NIL)
        }
        should("should return OR and EOF") {
            sourceShouldScanTo("or", TokenType.OR)
        }
        should("should return IMPORT and EOF") {
            sourceShouldScanTo("import", TokenType.IMPORT)
        }
        should("should return RETURN and EOF") {
            sourceShouldScanTo("return", TokenType.RETURN)
        }
        should("should return SUPER and EOF") {
            sourceShouldScanTo("super", TokenType.SUPER)
        }
        should("should return THIS and EOF") {
            sourceShouldScanTo("this", TokenType.THIS)
        }
        should("should return TRUE and EOF") {
            sourceShouldScanTo("true", TokenType.TRUE)
        }
        should("should return WHILE and EOF") {
            sourceShouldScanTo("while", TokenType.WHILE)
        }
    }

    context("special characters") {
        should("should return LEFT_PAREN and EOF") {
            sourceShouldScanTo("(", TokenType.LEFT_PAREN)
        }
        should("should return RIGHT_PAREN and EOF") {
            sourceShouldScanTo(")", TokenType.RIGHT_PAREN)
        }
        should("should return LEFT_BRACE and EOF") {
            sourceShouldScanTo("{", TokenType.LEFT_BRACE)
        }
        should("should return RIGHT_BRACE and EOF") {
            sourceShouldScanTo("}", TokenType.RIGHT_BRACE)
        }
        should("should return LEFT_BRACKET and EOF") {
            sourceShouldScanTo("[", TokenType.LEFT_BRACKET)
        }
        should("should return RIGHT_BRACKET and EOF") {
            sourceShouldScanTo("]", TokenType.RIGHT_BRACKET)
        }
        should("should return COLON and EOF") {
            sourceShouldScanTo(":", TokenType.COLON)
        }
        should("should return COMMA and EOF") {
            sourceShouldScanTo(",", TokenType.COMMA)
        }
        should("should return DOT and EOF") {
            sourceShouldScanTo(".", TokenType.DOT)
        }
        should("should return MINUS and EOF") {
            sourceShouldScanTo("-", TokenType.MINUS)
        }
        should("should return PLUS and EOF") {
            sourceShouldScanTo("+", TokenType.PLUS)
        }
        should("should return STAR and EOF") {
            sourceShouldScanTo("*", TokenType.STAR)
        }

        should("should return BANG and EOF") {
            sourceShouldScanTo("!", TokenType.BANG)
        }

        should("should return BANG_EQUAL and EOF") {
            sourceShouldScanTo("!=", TokenType.BANG_EQUAL)
        }

        should("should return EQUAL and EOF") {
            sourceShouldScanTo("=", TokenType.EQUAL)
        }

        should("should return EQUAL_EQUAL and EOF") {
            sourceShouldScanTo("==", TokenType.EQUAL_EQUAL)
        }

        should("should return LESS and EOF") {
            sourceShouldScanTo("<", TokenType.LESS)
        }

        should("should return LESS_EQUAL and EOF") {
            sourceShouldScanTo("<=", TokenType.LESS_EQUAL)
        }

        should("should return GREATER and EOF") {
            sourceShouldScanTo(">", TokenType.GREATER)
        }

        should("should return GREATER_EQUAL and EOF") {
            sourceShouldScanTo(">=", TokenType.GREATER_EQUAL)
        }
    }
})

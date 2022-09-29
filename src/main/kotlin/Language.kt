package pl.bfelis.fc93.language

import pl.bfelis.fc93.language.interpreter.Interpreter
import pl.bfelis.fc93.language.parser.Parser
import pl.bfelis.fc93.language.resolver.Resolver
import pl.bfelis.fc93.language.scanner.Scanner
import pl.bfelis.fc93.language.scanner.Token
import pl.bfelis.fc93.language.scanner.TokenType

class Language {
    private val interpreter = Interpreter()

    fun run(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        if (hadError) return

        val parser = Parser(tokens)
        val statements = parser.parse()

        if (hadError) return

        Resolver(interpreter).resolve(statements)

        if (hadError) return

        interpreter.interpret(statements)
    }

    fun getEnvironment() = interpreter.globals

    companion object {
        var hadError = false

        fun warn(token: Token, message: String?) {
            report(token.line, "", message ?: "")
        }

        fun error(line: Int, message: String?) {
            report(line, "", message ?: "")
            hadError = true
        }

        fun error(token: Token, message: String?) {
            if (token.type == TokenType.EOF) {
                report(token.line, " at end", message ?: "")
            } else {
                report(token.line, " at '${token.lexeme}'", message ?: "")
            }
            hadError = true
        }

        private fun report(
            line: Int,
            where: String,
            message: String
        ) {
            System.err.println(
                "[line $line] Error$where: $message"
            )
        }
    }
}

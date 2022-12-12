package pl.bfelis.fc93.language.error

import pl.bfelis.fc93.language.scanner.TokenType

class ErrorPrinter : ErrorObserver {
    override fun onErrors(errors: List<GeneralLException>) {
        errors.forEach { printError(it) }
    }

    private fun printError(error: GeneralLException) {
        if (error.token == null) {
            println("[line ${error.line}] ${error.errorMessage}")
        } else {
            if (error.token.type == TokenType.EOF) {
                println("[line ${error.line}] at end - ${error.errorMessage}")
            } else {
                println("[line ${error.line}] at '${error.token.lexeme}' - ${error.errorMessage}")
            }
        }
    }
}

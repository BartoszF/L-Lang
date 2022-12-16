package pl.bfelis.llang.language.error

import pl.bfelis.llang.language.scanner.TokenType

class ErrorPrinter : ErrorObserver {
    override fun onErrors(errors: List<GeneralLException>) {
        errors.forEach { printError(it) }
    }

    private fun printError(error: GeneralLException) {
        val errorType = error.javaClass.simpleName.padEnd(16)
        if (error.token == null) {
            println("$errorType: [line ${error.line}] ${error.errorMessage}")
        } else {
            if (error.token.type == TokenType.EOF) {
                println("$errorType: [line ${error.line}] at end - ${error.errorMessage}")
            } else {
                println("$errorType: [line ${error.line}] at '${error.token.lexeme}' - ${error.errorMessage}")
            }
        }
    }
}

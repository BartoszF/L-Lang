package pl.bfelis.llang.language.error

import pl.bfelis.llang.language.scanner.TokenType

class ErrorPrinter : ErrorObserver {
    override fun onErrors(errors: List<GeneralLException>) {
        errors.forEach { printError(it) }
        println()
    }

    private fun printError(error: GeneralLException) {
        val errorType = error.javaClass.simpleName.padEnd(16)
        val file = if (!error.fileName.isNullOrEmpty()) "${error.fileName}" else ""
        if (error.token == null) {
            println("$errorType: [$file@line ${error.line}] ${error.errorMessage}")
        } else {
            if (error.token.type == TokenType.EOF) {
                println("$errorType: [$file@line ${error.line}] at end - ${error.errorMessage}")
            } else {
                println("$errorType: [$file@line ${error.line}] at '${error.token.lexeme}' - ${error.errorMessage}")
            }
        }
    }
}

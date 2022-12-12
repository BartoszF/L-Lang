package pl.bfelis.fc93.language.error

import pl.bfelis.fc93.language.scanner.Token

open class GeneralLException(val line: Int, val token: Token?, val errorMessage: String? = null, throwable: Throwable? = null) :
    RuntimeException("[line $line] $errorMessage", throwable)

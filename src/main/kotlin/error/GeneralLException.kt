package pl.bfelis.llang.language.error

import pl.bfelis.llang.language.scanner.Token

open class GeneralLException(val line: Int, val token: Token?, val errorMessage: String? = null, throwable: Throwable? = null) :
    RuntimeException("[line $line] $errorMessage", throwable)

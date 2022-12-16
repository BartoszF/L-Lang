package pl.bfelis.llang.language.error

import pl.bfelis.llang.language.scanner.Token

open class GeneralLException(
    val line: Int,
    val token: Token?,
    val errorMessage: String? = null,
    val fileName: String? = null,
    throwable: Throwable? = null
) :
    RuntimeException(
        "${
        if (!fileName.isNullOrEmpty()) {
            "[$fileName]"
        } else ""
        } [line $line] $errorMessage",
        throwable
    )

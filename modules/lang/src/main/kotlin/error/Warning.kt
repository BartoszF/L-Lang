package pl.bfelis.llang.language.error

import pl.bfelis.llang.language.scanner.Token

class Warning(token: Token?, message: String, fileName: String? = null) :
    GeneralLException(token?.line ?: 0, token, message, fileName)

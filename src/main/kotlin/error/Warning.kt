package pl.bfelis.llang.language.error

import pl.bfelis.llang.language.scanner.Token

class Warning(token: Token?, message: String) : GeneralLException(token?.line ?: 0, token, message)

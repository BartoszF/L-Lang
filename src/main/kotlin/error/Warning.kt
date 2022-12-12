package pl.bfelis.fc93.language.error

import pl.bfelis.fc93.language.scanner.Token

class Warning(token: Token?, message: String) : GeneralLException(token?.line ?: 0, token, message)

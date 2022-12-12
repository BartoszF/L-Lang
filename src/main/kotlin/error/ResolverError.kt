package pl.bfelis.fc93.language.error

import pl.bfelis.fc93.language.scanner.Token

class ResolverError(token: Token, message: String? = null) : GeneralLException(token.line, token, message)

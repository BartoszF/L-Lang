package pl.bfelis.llang.language.error

import pl.bfelis.llang.language.scanner.Token

class ResolverError(token: Token, message: String? = null, fileName: String? = null) : GeneralLException(token.line, token, message, fileName)

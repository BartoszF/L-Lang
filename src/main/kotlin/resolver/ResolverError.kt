package pl.bfelis.fc93.language.resolver

import pl.bfelis.fc93.language.scanner.Token

class ResolverError(val name: Token, message: String? = null) : RuntimeException(message)

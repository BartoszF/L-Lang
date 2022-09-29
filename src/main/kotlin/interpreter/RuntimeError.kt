package pl.bfelis.fc93.language.interpreter

import pl.bfelis.fc93.language.scanner.Token

class RuntimeError(val token: Token?, message: String?) : RuntimeException(message)

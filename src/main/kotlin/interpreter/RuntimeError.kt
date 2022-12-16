package pl.bfelis.llang.language.interpreter

import pl.bfelis.llang.language.scanner.Token

class RuntimeError(val token: Token?, message: String?) : RuntimeException(message)

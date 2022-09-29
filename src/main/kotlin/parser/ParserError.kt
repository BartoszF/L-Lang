package pl.bfelis.fc93.language.parser

import pl.bfelis.fc93.language.scanner.Token

class ParserError(val lexeme: Token?, message: String) : RuntimeException(message)

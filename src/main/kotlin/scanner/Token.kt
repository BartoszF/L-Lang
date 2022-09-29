package pl.bfelis.fc93.language.scanner

data class Token(val type: TokenType, val lexeme: String, val literal: Any?, val line: Int)

package pl.bfelis.llang.language.scanner

data class Token(val type: TokenType, val lexeme: String, val literal: Any?, val line: Int)

package pl.bfelis.fc93.language.scanner

class ScanError(val line: Int, message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)

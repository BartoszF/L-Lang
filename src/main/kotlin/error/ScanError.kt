package pl.bfelis.fc93.language.error

class ScanError(line: Int, message: String, throwable: Throwable? = null) : GeneralLException(line, null, message, throwable)

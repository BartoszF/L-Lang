package pl.bfelis.llang.language.error

class ScanError(line: Int, message: String, throwable: Throwable? = null) : GeneralLException(line, null, message, throwable)

package pl.bfelis.llang.language.error

class ScanError(line: Int, message: String, fileName: String? = null, throwable: Throwable? = null) : GeneralLException(line, null, message, fileName, throwable)

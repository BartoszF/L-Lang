package pl.bfelis.llang.language.interpreter.lnative

interface LIterable {
    fun next(): Any?
    fun getIndex(): Double
    fun size(): Double
    fun atEnd(): Boolean
}

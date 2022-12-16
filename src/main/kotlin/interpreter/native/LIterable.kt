package pl.bfelis.llang.language.interpreter.native

interface LIterable {
    fun at(index: Any): Any?
    fun set(index: Any, value: Any?): Any?
}

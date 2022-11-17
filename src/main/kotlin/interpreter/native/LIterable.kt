package pl.bfelis.fc93.language.interpreter.native

interface LIterable {
    fun at(index: Any): Any?
    fun set(index: Any, value: Any?): Any?
}

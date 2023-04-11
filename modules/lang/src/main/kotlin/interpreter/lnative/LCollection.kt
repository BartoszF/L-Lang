package pl.bfelis.llang.language.interpreter.lnative

interface LCollection<T> : HasIterator {
    fun at(index: Any): Any?
    fun set(index: Any, value: Any?): Any?
    fun map(function: (Any?) -> Any?): T?
    fun forEach(function: (Any?) -> Unit)
}

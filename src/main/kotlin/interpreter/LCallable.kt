package pl.bfelis.llang.language.interpreter

interface LCallable {
    fun arity(): Int
    fun call(interpreter: Interpreter, arguments: List<Any?>): Any?
}

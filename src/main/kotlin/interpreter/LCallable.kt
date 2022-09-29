package pl.bfelis.fc93.language.interpreter

interface LCallable {
    fun arity(): Int
    fun call(interpreter: Interpreter, arguments: List<Any?>): Any?
}

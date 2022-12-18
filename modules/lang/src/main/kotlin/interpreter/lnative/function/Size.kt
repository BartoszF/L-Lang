package pl.bfelis.llang.language.interpreter.lnative.function

import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.interpreter.LCallable

object Size : LCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val str = arguments[0] as String
        return str.length.toDouble()
    }
}

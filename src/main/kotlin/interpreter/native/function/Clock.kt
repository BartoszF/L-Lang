package pl.bfelis.fc93.language.interpreter.native.function

import pl.bfelis.fc93.language.interpreter.Interpreter
import pl.bfelis.fc93.language.interpreter.LCallable

object Clock : LCallable {
    override fun arity(): Int = 0

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return System.currentTimeMillis().toDouble() / 1000.0
    }

    override fun toString(): String {
        return "<native fn : clock>"
    }
}

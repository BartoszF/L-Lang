package pl.bfelis.fc93.language.interpreter.native.function

import pl.bfelis.fc93.language.interpreter.Interpreter
import pl.bfelis.fc93.language.interpreter.LCallable
import pl.bfelis.fc93.language.interpreter.Utils

object ToString : LCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return Utils.stringify(arguments[0])
    }

    override fun toString(): String {
        return "<native fn : string>"
    }
}

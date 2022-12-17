package pl.bfelis.llang.language.interpreter.native.function

import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.interpreter.LCallable
import pl.bfelis.llang.language.interpreter.Utils

object ToString : LCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return Utils.stringify(arguments[0])
    }

    override fun toString(): String {
        return "<native fn : string>"
    }
}

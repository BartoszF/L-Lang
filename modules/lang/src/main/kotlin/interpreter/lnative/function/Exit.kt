package pl.bfelis.llang.language.interpreter.lnative.function

import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.interpreter.LCallable
import kotlin.system.exitProcess

object Exit : LCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        exitProcess((arguments[0] as Number).toInt())
    }

    override fun toString(): String {
        return "<native fn : exit>"
    }
}

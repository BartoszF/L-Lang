package pl.bfelis.llang.language.interpreter.lnative.function

import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.interpreter.LCallable
import pl.bfelis.llang.language.interpreter.Utils

object PrintLine : LCallable {
    override fun arity(): Int {
        return 1
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val str = Utils.stringify(arguments[0])

        println(str)

        return null
    }
}

package pl.bfelis.llang.language.interpreter.native.function

import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.interpreter.LCallable
import pl.bfelis.llang.language.interpreter.Utils

object Print : LCallable {
    override fun arity(): Int {
        return 1
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val str = Utils.stringify(arguments[0])

        print(str)

        return null
    }
}

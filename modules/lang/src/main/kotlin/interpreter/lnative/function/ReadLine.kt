package pl.bfelis.llang.language.interpreter.lnative.function

import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.interpreter.LCallable

object ReadLine : LCallable {
    override fun arity(): Int {
        return 0
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        return readLine()
    }
}

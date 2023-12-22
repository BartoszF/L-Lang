package pl.bfelis.llang.language.interpreter.lnative.function

import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.interpreter.LCallable
import pl.bfelis.llang.language.interpreter.lnative.klass.iterators.LRange
import pl.bfelis.llang.language.interpreter.lnative.klass.iterators.RangeInstance

object Range : LCallable {
    override fun arity(): Int = 2

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val start = (arguments[0] as Number).toDouble()
        val end = (arguments[1] as Number).toDouble()
        return RangeInstance(LRange(interpreter.environment), start, end)
    }
}

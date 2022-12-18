package pl.bfelis.llang.language.interpreter.native.klass

import pl.bfelis.llang.language.interpreter.*
import pl.bfelis.llang.language.interpreter.native.LIterable

val ArrayMethods = { env: Environment ->
    mutableMapOf(
        "init" to
            getNativeMethodForLClass("init", listOf("size"), true, env),
        "size" to getNativeMethodForLClass("size", emptyList(), false, env)
    )
}

class LArray(env: Environment) : LNativeClass("Array", null, ArrayMethods(env)) {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val size = (arguments[0] as Double).toInt()
        return ArrayInstance(this, size)
    }
}

class ArrayInstance(klass: LArray, size: Int) : LNativeInstance(klass), LIterable {
    private val array = Array<Any?>(size) { null }

    override fun nativeFn(name: String): Any {
        return when (name) {
            "size" -> object : LCallable {
                override fun arity(): Int {
                    return 0
                }

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                    return array.size
                }
            }

            else -> {
                throw RuntimeError(null, "Unknown method $name")
            }
        }
    }

    override fun at(index: Any): Any? {
        if (index !is Double) throw RuntimeError(null, "Index not a number")
        return array[index.toInt()]
    }

    override fun set(index: Any, value: Any?): Any? {
        if (index !is Double) throw RuntimeError(null, "Index not a number")
        array[index.toInt()] = value
        return array[index.toInt()]
    }
}

package pl.bfelis.llang.language.interpreter.lnative.klass.collections

import pl.bfelis.llang.language.interpreter.*
import pl.bfelis.llang.language.interpreter.lnative.LCollection
import pl.bfelis.llang.language.interpreter.lnative.LIterable
import pl.bfelis.llang.language.interpreter.lnative.klass.getNativeMethodForLClass

val ArrayMethods = { env: Environment ->
    mutableMapOf(
        "init" to
            getNativeMethodForLClass("init", listOf("size"), true, env),
        "size" to getNativeMethodForLClass("size", emptyList(), false, env),
        "iterator" to getNativeMethodForLClass("iterator", emptyList(), false, env)
    )
}

class LArray(env: Environment) : LNativeClass("Array", null, env, ArrayMethods(env)) {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val size = (arguments[0] as Double).toInt()
        return ArrayInstance(this, size)
    }
}

class ArrayInstance(klass: LArray, val array: Array<Any?> = emptyArray()) :
    LNativeInstance(klass),
    LCollection<ArrayInstance> {
    private val env = klass.env

    constructor(klass: LArray, size: Int) : this(klass, Array<Any?>(size) { null })

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

            "iterator" -> object : LCallable {
                override fun arity(): Int {
                    return 0
                }

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                    return iterator()
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

    override fun map(function: (Any?) -> Any?): ArrayInstance {
        val mapped = array.map(function).toTypedArray()
        return ArrayInstance(LArray(env), mapped)
    }

    override fun forEach(function: (Any?) -> Unit) {
        array.forEach(function)
    }

    override fun iterator(): LIterable {
        return ArrayIterator(array)
    }

    class ArrayIterator(private val array: Array<Any?>) : LIterable {
        var cursor = 0
        override fun next(): Any? {
            return array[cursor++]
        }

        override fun getIndex(): Double {
            return cursor.toDouble()
        }

        override fun size(): Double {
            return array.size.toDouble()
        }

        override fun atEnd(): Boolean {
            return getIndex() == size()
        }
    }
}

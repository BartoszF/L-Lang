package pl.bfelis.llang.language.interpreter.lnative.klass.collections

import pl.bfelis.llang.language.interpreter.*
import pl.bfelis.llang.language.interpreter.lnative.LIterable
import pl.bfelis.llang.language.interpreter.lnative.klass.getNativeMethodForLClass

val ListMethods = { env: Environment ->
    mutableMapOf(
        "init" to
            getNativeMethodForLClass("init", emptyList(), true, env),
        "size" to getNativeMethodForLClass("size", emptyList(), false, env),
        "add" to getNativeMethodForLClass("add", listOf("element"), false, env),
        "removeAt" to getNativeMethodForLClass("removeAt", listOf("index"), false, env)
    )
}

class LList(env: Environment) : LNativeClass("List", null, env, ListMethods(env)) {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return ListInstance(this)
    }
}

class ListInstance(klass: LList) : LNativeInstance(klass), LIterable {
    private val list = mutableListOf<Any?>()

    override fun nativeFn(name: String): Any {
        return when (name) {
            "size" -> object : LCallable {
                override fun arity(): Int {
                    return 0
                }

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                    return list.size.toDouble()
                }
            }

            "add" -> object : LCallable {
                override fun arity(): Int {
                    return 1
                }

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    list.add(arguments[0])
                    return arguments[0]
                }
            }

            "removeAt" -> object : LCallable {
                override fun arity(): Int {
                    return 1
                }

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    val index = arguments[0] as Double
                    return list.removeAt(index.toInt())
                }
            }

            else -> {
                throw RuntimeError(null, "Unknown method $name")
            }
        }
    }

    override fun at(index: Any): Any? {
        if (index !is Double) throw RuntimeError(null, "Index not a number")
        return list[index.toInt()]
    }

    override fun set(index: Any, value: Any?): Any? {
        if (index !is Double) throw RuntimeError(null, "Index not a number")
        list[index.toInt()] = value
        return list[index.toInt()]
    }

    fun addAll(other: List<Any?>) {
        list.addAll(other)
    }
}

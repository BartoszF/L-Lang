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
        "removeAt" to getNativeMethodForLClass("removeAt", listOf("index"), false, env),
        "map" to getNativeMethodForLClass("map", listOf("function"), false, env),
        "each" to getNativeMethodForLClass("each", listOf("function"), false, env)
    )
}

class LList(env: Environment) : LNativeClass("List", null, env, ListMethods(env)) {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return ListInstance(this)
    }
}

class ListInstance(klass: LList, val list: MutableList<Any?> = mutableListOf()) : LNativeInstance(klass), LIterable<ListInstance> {
    private val env = klass.env

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

            "map" -> object : LCallable {
                override fun arity(): Int = 1

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                    val function = arguments[0] as LFunction

                    return map { function.call(interpreter, listOf(it)) }
                }
            }

            "each" -> object : LCallable {
                override fun arity(): Int = 1

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                    val function = arguments[0] as LFunction

                    forEach { function.call(interpreter, listOf(it)) }

                    return Unit
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

    override fun map(function: (Any?) -> Any?): ListInstance {
        val mapped = list.map(function).toMutableList()
        return ListInstance(LList(env), mapped)
    }

    override fun forEach(function: (Any?) -> Unit) {
        list.forEach(function)
    }

    fun addAll(other: List<Any?>) {
        list.addAll(other)
    }
}

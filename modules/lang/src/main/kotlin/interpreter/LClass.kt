package pl.bfelis.llang.language.interpreter

import pl.bfelis.llang.language.scanner.Token

open class LClass(
    val name: String,
    val superclass: LClass? = null,
    val methods: MutableMap<String, LFunction> = mutableMapOf(),
    val staticMethods: MutableMap<String, LFunction> = mutableMapOf()
) : LCallable {
    override fun arity(): Int {
        val initializer = findMethod("init") ?: return 0
        return initializer.arity()
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val instance = LInstance(this)
        val initializer = findMethod("init")
        initializer?.bind(instance)?.call(interpreter, arguments)
        return instance
    }

    open fun findMethod(name: String): LFunction? {
        if (methods.containsKey(name)) return methods[name]
        if (staticMethods.containsKey(name)) return staticMethods[name]

        if (superclass != null) {
            return superclass.findMethod(name)
        }

        return null
    }

    operator fun get(name: Token): Any? {
//        if (fields.containsKey(name.lexeme)) {
//            return fields[name.lexeme]
//        } TODO: Might be usable for static fields

        val method = findMethod(name.lexeme)
        if (method != null) return method // .bind(this)

        throw RuntimeError(name, "Undefined property '${name.lexeme}'.")
    }

    override fun toString(): String {
        return name
    }
}

open class LNativeClass(
    name: String,
    superclass: LClass? = null,
    methods: MutableMap<String, LFunction> = mutableMapOf(),
    staticMethods: MutableMap<String, LFunction> = mutableMapOf()
) : LClass(name, superclass, methods, staticMethods) {

    override fun findMethod(name: String): LFunction? {
        if (methods.containsKey(name)) return methods[name]
        if (staticMethods.containsKey(name)) return nativeStaticFn(name)

        if (superclass != null) {
            return superclass.findMethod(name)
        }

        return null
    }

    open fun nativeStaticFn(name: String): LFunction? {
        return null
    }
}

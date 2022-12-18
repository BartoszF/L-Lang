package pl.bfelis.llang.language.interpreter

import pl.bfelis.llang.language.scanner.Token

open class LInstance(val klass: LClass, val fields: MutableMap<String, Any?> = mutableMapOf()) {

    open operator fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme]
        }
        val method = klass.findMethod(name.lexeme)
        if (method != null) return method.bind(this)

        throw RuntimeError(name, "Undefined property '${name.lexeme}'.")
    }

    open operator fun get(name: String): Any? {
        if (fields.containsKey(name)) {
            return fields[name]
        }
        val method = klass.findMethod(name)
        if (method != null) return method.bind(this)

        throw RuntimeError(null, "Undefined property '$name'.")
    }

    fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }

    override fun toString(): String {
        return "${klass.name} instance"
    }
}

open class LNativeInstance(klass: LNativeClass, fields: MutableMap<String, Any?> = mutableMapOf()) :
    LInstance(klass, fields) {
    override operator fun get(name: Token): Any? {
        return getMethod(name.lexeme)
    }

    override operator fun get(name: String): Any? {
        return getMethod(name)
    }

    private fun getMethod(name: String): Any? {
        if (fields.containsKey(name)) {
            return fields[name]
        }
        val method = klass.findMethod(name)
        if (method != null) {
            if (method.isNative) {
                return nativeFn(name)
            }

            return method.bind(this)
        }

        throw RuntimeError(null, "Undefined property '$name'.")
    }

    open fun nativeFn(name: String): Any? {
        return null
    }
}

package pl.bfelis.llang.language.interpreter

import pl.bfelis.llang.language.scanner.Token

data class Environment(val enclosing: Environment? = null, val values: MutableMap<String, Any?> = mutableMapOf()) {
    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }

        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'")
    }

    fun assignAt(distance: Int, name: Token, value: Any?) {
        ancestor(distance).assign(name, value)
    }

    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }

        if (enclosing != null) return enclosing.get(name)

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'")
    }

    fun getByName(name: String): Any? {
        if (values.containsKey(name)) {
            return values[name]
        }

        if (enclosing != null) return enclosing.getByName(name)

        throw RuntimeError(null, "Undefined variable '$name'")
    }

    fun getAt(distance: Int, name: String): Any? {
        return ancestor(distance).values[name]
    }

    fun ancestor(distance: Int): Environment {
        var environment: Environment = this
        for (i in 0 until distance) {
            environment = environment.enclosing!!
        }
        return environment
    }
}

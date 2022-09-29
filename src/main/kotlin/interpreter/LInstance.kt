package pl.bfelis.fc93.language.interpreter

import pl.bfelis.fc93.language.scanner.Token

class LInstance(private val klass: LClass, val fields: MutableMap<String, Any?> = mutableMapOf()) {

    operator fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme]
        }
        val method = klass.findMethod(name.lexeme)
        if (method != null) return method.bind(this)

        throw RuntimeError(name, "Undefined property '${name.lexeme}'.")
    }

    fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }

    override fun toString(): String {
        return "${klass.name} instance"
    }
}

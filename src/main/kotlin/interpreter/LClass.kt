package pl.bfelis.llang.language.interpreter

open class LClass(
    val name: String,
    val superclass: LClass? = null,
    val methods: MutableMap<String, LFunction> = mutableMapOf()
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

    fun findMethod(name: String): LFunction? {
        if (methods.containsKey(name)) return methods[name]

        if (superclass != null) {
            return superclass.findMethod(name)
        }

        return null
    }

    override fun toString(): String {
        return name
    }
}

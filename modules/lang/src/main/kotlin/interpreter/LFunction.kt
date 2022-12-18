package pl.bfelis.llang.language.interpreter

import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.interpreter.flow.Return

open class LFunction(
    private val declaration: Statement.Function,
    private val closure: Environment,
    private val isInitializer: Boolean = false,
    val isNative: Boolean = false,
    val isStatic: Boolean = false
) : LCallable {

    constructor(function: LFunction) : this(
        function.declaration,
        function.closure,
        function.isInitializer,
        function.isNative,
        function.isStatic
    )

    override fun arity(): Int = declaration.params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(closure)
        for (i in 0 until declaration.params.size) {
            environment.define(
                declaration.params[i].lexeme,
                arguments[i]
            )
        }

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (retValue: Return) {
            if (isInitializer) return closure.getAt(0, "this")
            return retValue.value
        }

        if (isInitializer) return closure.getAt(0, "this")

        return null
    }

    fun bind(instance: LInstance): LFunction {
        val environment = Environment(closure)
        environment.define("this", instance)
        return LFunction(declaration, environment, isInitializer)
    }

    override fun toString(): String {
        return "<fn ${if (isStatic) "static" else ""} ${declaration.name.lexeme}>"
    }
}

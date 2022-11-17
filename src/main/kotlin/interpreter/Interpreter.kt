package pl.bfelis.fc93.language.interpreter

import pl.bfelis.fc93.language.ast.Expr
import pl.bfelis.fc93.language.ast.Statement
import pl.bfelis.fc93.language.interpreter.native.LIterable
import pl.bfelis.fc93.language.parser.Return
import pl.bfelis.fc93.language.scanner.Token
import pl.bfelis.fc93.language.scanner.TokenType

class Interpreter : Expr.Visitor<Any?>, Statement.Visitor<Unit> {

    val globals = Environment()
    private var environment = globals
    private val locals: MutableMap<Expr, Int> = mutableMapOf()

    init {
        Globals.values(globals).map {
            globals.define(it.key, it.value)
        }
    }

    fun interpret(statements: List<Statement?>) {
        try {
            statements.forEach { it?.let { statement -> execute(statement) } }
        } catch (error: RuntimeError) {
            throw error
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                return (left as Double) - (right as Double)
            }

            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    return left + right
                }
                if (left is String && right is String) {
                    return left + right
                }

                throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
            }

            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) / (right as Double)
            }

            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) * (right as Double)
            }

            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) > (right as Double)
            }

            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) > (right as Double)
            }

            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }

            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }

            TokenType.BANG -> return !isEqual(left, right)
            TokenType.BANG_EQUAL -> return isEqual(left, right)
            else -> {
                throw RuntimeError(expr.operator, "Wrong binary expresion")
            } // TODO: More exhaustive error
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG -> !Utils.isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }

            else -> {
                throw RuntimeError(expr.operator, "Wrong unary expression")
            }
        }
    }

    override fun visitExpressionStatement(statement: Statement.Expression) {
        evaluate(statement.expression)
    }

    override fun visitPrintStatement(statement: Statement.Print) {
        val value = evaluate(statement.expression)
        println(Utils.stringify(value))
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        return lookupVariable(expr.name, expr) // environment.get(expr.name)
    }

    override fun visitVarStatement(statement: Statement.Var) {
        var value: Any? = null
        if (statement.initializer != null) {
            value = evaluate(statement.initializer)
        }

        environment.define(statement.name.lexeme, value)
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)

        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, value)
        } else {
            globals.assign(expr.name, value)
        }

        return value
    }

    override fun visitBlockStatement(statement: Statement.Block) {
        executeBlock(statement.statements, Environment(environment))
    }

    override fun visitIfStatement(statement: Statement.If) {
        if (Utils.isTruthy(evaluate(statement.condition))) {
            execute(statement.thenBranch)
        } else if (statement.elseBranch != null) {
            execute(statement.elseBranch)
        }
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type === TokenType.OR) {
            if (Utils.isTruthy(left)) return left
        } else {
            if (!Utils.isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitWhileStatement(statement: Statement.While) {
        while (Utils.isTruthy(evaluate(statement.condition))) {
            execute(statement.body)
        }
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)

        val arguments: MutableList<Any?> = ArrayList()
        for (argument in expr.arguments) {
            arguments.add(evaluate(argument))
        }

        if (callee !is LCallable) {
            throw RuntimeError(
                expr.paren,
                "Can only call functions and classes."
            )
        }

        val function: LCallable = callee
        if (arguments.size != function.arity()) {
            throw RuntimeError(
                expr.paren,
                "Expected ${function.arity()} arguments but got ${arguments.size}."
            )
        }

        return function.call(this, arguments)
    }

    override fun visitFunctionStatement(statement: Statement.Function) {
        val function = LFunction(statement, environment)
        environment.define(statement.name.lexeme, function)
    }

    override fun visitReturnStatement(statement: Statement.Return) {
        var value: Any? = null
        if (statement.value != null) value = evaluate(statement.value)

        throw Return(value)
    }

    override fun visitClassStatement(statement: Statement.Class) {
        var superClass: Any? = null
        if (statement.superclass != null) {
            superClass = evaluate(statement.superclass)
            if (superClass !is LClass) {
                throw RuntimeError(statement.superclass.name, "Superclass must be a class.")
            }
        }
        environment.define(statement.name.lexeme, null)

        if (statement.superclass != null) {
            environment = Environment(environment)
            environment.define("super", superClass)
        }

        val methods = mutableMapOf<String, LFunction>()
        for (method in statement.methods) {
            val function = LFunction(method, environment, method.name.lexeme == "init")
            methods[method.name.lexeme] = function
        }

        val klass = LClass(statement.name.lexeme, superClass as LClass?, methods)

        if (superClass != null) {
            environment = environment.enclosing!!
        }
        environment.assign(statement.name, klass)
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        val obj = evaluate(expr.obj)
        if (obj is LInstance) {
            return obj[expr.name]
        }

        println(obj)
        throw RuntimeError(
            expr.name,
            "Only instances have properties."
        )
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        val obj = evaluate(expr.obj) as? LInstance ?: throw RuntimeError(
            expr.name,
            "Only instances have fields."
        )

        val value = evaluate(expr.value)
        obj.set(expr.name, value)
        return value
    }

    override fun visitSuperExpr(expr: Expr.Super): Any {
        val distance = locals[expr]!!
        val superclass = environment.getAt(
            distance,
            "super"
        ) as LClass

        val obj = environment.getAt(distance - 1, "this") as LInstance

        val method = superclass.findMethod(expr.method.lexeme)
            ?: throw RuntimeError(expr.method, "Undefined property '${expr.method.lexeme}'.")

        return method.bind(obj)
    }

    override fun visitAccessorExpr(expr: Expr.Accessor): Any? {
        val variable = evaluate(expr.obj)
        val accessor = try {
            (evaluate(expr.accessor) as Double)
        } catch (ex: Exception) {
            throw RuntimeError(expr.accessorToken, "Wrong data type for accessor.")
        }

        if (variable is Array<*>) {
            return variable[accessor.toInt()]
        }

        if (variable is List<*>) {
            return variable[accessor.toInt()]
        }

        if (variable is CharSequence) {
            return variable[accessor.toInt()]
        }

        if (variable is LIterable) {
            return variable.at(accessor)
        }

        throw RuntimeError(expr.accessorToken, "Variable is not iterable.")
    }

    override fun visitAccessorSetExpr(expr: Expr.AccessorSet): Any? {
        val variable = evaluate(expr.accessor.obj)
        val accessor = try {
            (evaluate(expr.accessor.accessor) as Double)
        } catch (ex: Exception) {
            throw RuntimeError(expr.accessor.accessorToken, "Wrong data type for accessor.")
        }

        val value = evaluate(expr.value)

        if (variable is LIterable) {
            return variable.set(accessor, value)
        }

        if (variable is Array<*>) {
            @Suppress("UNCHECKED_CAST")
            (variable as Array<Any?>)[accessor.toInt()] = value
            return variable
        }

        if (variable is MutableList<*>) {
            @Suppress("UNCHECKED_CAST")
            (variable as MutableList<Any?>)[accessor.toInt()] = value
            return variable
        }

        if (variable is String) {
            val array = variable.toCharArray()
            array[accessor.toInt()] = if (value is String) value[0] else value as Char
            val newString = String(array)
            environment.assign((expr.accessor.obj as Expr.Variable).name, newString)
            return variable
        }

        throw RuntimeError(expr.accessor.accessorToken, "Variable is not iterable.")
    }

    override fun visitThisExpr(expr: Expr.This): Any? {
        return lookupVariable(expr.keyword, expr)
    }

    fun resolve(expr: Expr, depth: Int) {
        locals[expr] = depth
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun execute(statement: Statement) {
        statement.accept(this)
    }

    fun executeBlock(
        statements: List<Statement?>,
        environment: Environment?
    ) {
        val previous = this.environment
        try {
            this.environment = environment!!
            for (statement in statements) {
                statement?.let { execute(it) }
            }
        } finally {
            this.environment = previous
        }
    }

    fun lookupVariable(name: Token, expr: Expr): Any? {
        val distance = locals[expr]
        return if (distance != null) {
            environment.getAt(distance, name.lexeme)
        } else {
            globals.get(name)
        }
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        return if (a == null) false else a == b
    }

    private fun checkNumberOperand(operator: Token?, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(
        operator: Token,
        left: Any?,
        right: Any?
    ) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }
}

package pl.bfelis.fc93.language.interpreter

import pl.bfelis.fc93.language.ast.Expr
import pl.bfelis.fc93.language.ast.Statement
import pl.bfelis.fc93.language.parser.Return
import pl.bfelis.fc93.language.scanner.Token
import pl.bfelis.fc93.language.scanner.TokenType

class Interpreter : Expr.Visitor<Any?>, Statement.Visitor<Unit> {

    val globals = Environment()
    private var environment = globals
    private val locals: MutableMap<Expr, Int> = mutableMapOf()

    init {
        globals.define(
            "clock",
            object : LCallable {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    return System.currentTimeMillis().toDouble() / 1000.0
                }

                override fun toString(): String {
                    return "<native fn>"
                }
            }
        )
    }

    fun interpret(statements: List<Statement?>) {
        try {
            statements.forEach { it?.let { statement -> execute(statement) } }
        } catch (error: RuntimeError) {
            throw error // TODO: Multiple errors. Don't interrupt when running.
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
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
        }

        // Unreachable.
        return null
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            TokenType.BANG -> return !isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                return -(right as Double)
            }
        }

        // Unreachable.
        return null
    }

    override fun visitExpressionStatement(statement: Statement.Expression) {
        evaluate(statement.expression)
    }

    override fun visitPrintStatement(statement: Statement.Print) {
        val value = evaluate(statement.expression)
        println(stringify(value))
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
        if (isTruthy(evaluate(statement.condition))) {
            execute(statement.thenBranch)
        } else if (statement.elseBranch != null) {
            execute(statement.elseBranch)
        }
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type === TokenType.OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitWhileStatement(statement: Statement.While) {
        while (isTruthy(evaluate(statement.condition))) {
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
        environment.define(statement.name!!.lexeme, function)
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

    override fun visitSuperExpr(expr: Expr.Super): Any? {
        val distance = locals[expr]!!
        val superclass = environment.getAt(
            distance,
            "super"
        ) as LClass

        val obj = environment.getAt(distance - 1, "this") as LInstance

        val method = superclass.findMethod(expr.method.lexeme)

        if (method == null) {
            throw RuntimeError(expr.method, "Undefined property '${expr.method.lexeme}'.")
        }

        return method.bind(obj)
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

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        return if (obj is Boolean) obj else true
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

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"
        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }
        return obj.toString()
    }
}

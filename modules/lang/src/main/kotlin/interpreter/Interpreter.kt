package pl.bfelis.llang.language.interpreter

import pl.bfelis.llang.language.ast.Expr
import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.interpreter.flow.Break
import pl.bfelis.llang.language.interpreter.flow.Continue
import pl.bfelis.llang.language.interpreter.flow.Return
import pl.bfelis.llang.language.interpreter.lnative.Globals
import pl.bfelis.llang.language.interpreter.lnative.HasIterator
import pl.bfelis.llang.language.interpreter.lnative.LCollection
import pl.bfelis.llang.language.interpreter.lnative.LIterable
import pl.bfelis.llang.language.interpreter.lnative.klass.collections.LList
import pl.bfelis.llang.language.interpreter.lnative.klass.collections.ListInstance
import pl.bfelis.llang.language.scanner.Token
import pl.bfelis.llang.language.scanner.TokenType

class Interpreter : Expr.Visitor<Any?>, Statement.Visitor<Unit> {

    val globals = Environment()
    var environment = globals
    private val locals: MutableMap<Expr, Int> = mutableMapOf()

    init {
        Globals.values(globals).map {
            globals.define(it.key, it.value)
        }
    }

    fun interpret(statements: List<Statement?>, fileName: String?) {
        try {
            statements.forEach { it?.let { statement -> execute(statement, fileName) } }
        } catch (error: RuntimeError) {
            throw error
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary, fileName: String?): Any {
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
                return (left as Double) >= (right as Double)
            }

            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }

            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) <= (right as Double)
            }

            TokenType.BANG_EQUAL -> return !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> return isEqual(left, right)
            else -> {
                throw RuntimeError(expr.operator, "Wrong binary expresion")
            } // TODO: More exhaustive error
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping, fileName: String?): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal, fileName: String?): Any? {
        return expr.value
    }

    override fun visitListDefExpr(expr: Expr.ListDef, fileName: String?): Any {
        return ListInstance(LList(environment), expr.elements.map { evaluate(it) }.toMutableList())
    }

    override fun visitUnaryExpr(expr: Expr.Unary, fileName: String?): Any {
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

    override fun visitExpressionStatement(statement: Statement.Expression, fileName: String?) {
        evaluate(statement.expression)
    }

    override fun visitVariableExpr(expr: Expr.Variable, fileName: String?): Any? {
        return lookupVariable(expr.name, expr)
    }

    override fun visitIncrementExpr(expr: Expr.Increment, fileName: String?): Any {
        val value = lookupVariable(expr.name, expr)

        if (value !is Double) throw RuntimeError(expr.name, "Only numbers can be incremented")

        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, value + 1)
        } else {
            globals.assign(expr.name, value + 1)
        }

        return value
    }

    override fun visitDecrementExpr(expr: Expr.Decrement, fileName: String?): Any {
        val value = lookupVariable(expr.name, expr)

        if (value !is Double) throw RuntimeError(expr.name, "Only numbers can be decremented")

        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, value - 1)
        } else {
            globals.assign(expr.name, value - 1)
        }

        return value
    }

    override fun visitVarStatement(statement: Statement.Var, fileName: String?) {
        var value: Any? = null
        if (statement.initializer != null) {
            value = evaluate(statement.initializer)
        }

        environment.define(statement.name.lexeme, value)
    }

    override fun visitValStatement(statement: Statement.Val, fileName: String?) {
        var value: Any? = null
        if (statement.initializer != null) {
            value = evaluate(statement.initializer)
        }

        environment.define(statement.name.lexeme, value)
    }

    override fun visitListSpreadStatement(statement: Statement.ListSpread, fileName: String?) {
        val value = evaluate(statement.initializer)

        val iterator =
            when (value) {
                is LIterable -> {
                    value
                }

                is HasIterator -> {
                    value.iterator()
                }

                else -> {
                    throw RuntimeError(statement.names.last(), "Expect iterable as list destructure initializer.")
                }
            }

        val list = mutableListOf<Any?>()
        for (i in 0 until iterator.size().toInt()) {
            list.add(iterator.next())
        }

        val first = list.take(statement.names.size - 1)
        val rest = list.drop(statement.names.size - 1).toMutableList()

        for (i in 0 until statement.names.size - 1) {
            environment.define(statement.names[i].lexeme, first.getOrNull(i))
        }

        val mappedRest = if (rest.size == 0) null else ListInstance(LList(environment), rest)
        environment.define(statement.names.last().lexeme, mappedRest)
    }

    override fun visitAssignExpr(expr: Expr.Assign, fileName: String?): Any? {
        val value = evaluate(expr.value)

        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, value)
        } else {
            globals.assign(expr.name, value)
        }

        return value
    }

    override fun visitAssignIncrementExpr(expr: Expr.AssignIncrement, fileName: String?): Any {
        val value = evaluate(expr.value)
        val currentValue = lookupVariable(expr.name, expr)

        if (value !is Double) throw RuntimeError(expr.name, "Only numbers can be incremented")
        if (currentValue !is Double) throw RuntimeError(expr.name, "Only numbers can be incremented")

        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, currentValue + value)
        } else {
            globals.assign(expr.name, value)
        }

        return currentValue + value
    }

    override fun visitAssignDecrementExpr(expr: Expr.AssignDecrement, fileName: String?): Any {
        val value = evaluate(expr.value)
        val currentValue = lookupVariable(expr.name, expr)

        if (value !is Double) throw RuntimeError(expr.name, "Only numbers can be decremented")
        if (currentValue !is Double) throw RuntimeError(expr.name, "Only numbers can be decremented")

        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, currentValue - value)
        } else {
            globals.assign(expr.name, value)
        }

        return currentValue - value
    }

    override fun visitBlockStatement(statement: Statement.Block, fileName: String?) {
        executeBlock(statement.statements, Environment(environment))
    }

    override fun visitBreakStatement(statement: Statement.Break, fileName: String?) {
        throw Break()
    }

    override fun visitContinueStatement(statement: Statement.Continue, fileName: String?) {
        throw Continue()
    }

    override fun visitIfStatement(statement: Statement.If, fileName: String?) {
        if (Utils.isTruthy(evaluate(statement.condition))) {
            execute(statement.thenBranch)
        } else if (statement.elseBranch != null) {
            execute(statement.elseBranch)
        }
    }

    override fun visitInExpr(expr: Expr.In, fileName: String?) {
        val name = expr.name

//        val iteratorCall = Expr.Call(
//            Expr.Get(expr.iterable, Token(TokenType.IDENTIFIER, "iterator", null, name.line)),
//            name,
//            emptyList()
//        )
        var value = evaluate(expr.iterable)

        if (value is LIterable) {
            // Nothing to do
        } else if (value is HasIterator) {
            value = value.iterator()
        } else {
            throw RuntimeError(expr.name, "Provided iterable has no iterator!", fileName)
        }

        environment.define("${name.lexeme}_it", value)
        environment.define(name.lexeme, value.next())
    }

    override fun visitImportStatement(statement: Statement.Import, fileName: String?) {
        // Nothing to do here - everything is done in resolver.
    }

    override fun visitLogicalExpr(expr: Expr.Logical, fileName: String?): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type === TokenType.OR) {
            if (Utils.isTruthy(left)) return left
        } else {
            if (!Utils.isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitLambdaExpr(expr: Expr.Lambda, fileName: String?): Any {
        val func =
            Statement.Function(Token(TokenType.IDENTIFIER, "Lambda", null, expr.line), expr.params, expr.body, false)

        return LFunction(func, environment, false)
    }

    override fun visitSliceExpr(expr: Expr.Slice, fileName: String?): Any {
        var value = evaluate(expr.obj)

        if (value is LIterable) {
            // Nothing to do
        } else if (value is HasIterator) {
            value = value.iterator()
        } else {
            throw RuntimeError(expr.token, "Provided iterable has no iterator!", fileName)
        }

        val index: Double = expr.start?.let { evaluate(it) as Double } ?: 0.0
        val count: Double? = expr.count?.let { evaluate(it) as Double }

        val sliced = value.slice(index.toInt(), count?.toInt())

        return ListInstance(LList(environment), sliced)
    }

    override fun visitWhileStatement(statement: Statement.While, fileName: String?) {
        while (Utils.isTruthy(evaluate(statement.condition))) {
            try {
                execute(statement.body)
            } catch (br: Break) {
                return
            } catch (c: Continue) {
                // Do nothing
            }
        }
    }

    override fun visitForStatement(statement: Statement.For, fileName: String?) {
        val previous = this.environment
        try {
            this.environment = Environment(previous)
            statement.`in`.accept(this)

            val iterator = lookupVariable(
                Token(
                    TokenType.IDENTIFIER,
                    "${statement.`in`.name.lexeme}_it",
                    null,
                    statement.`in`.name.line
                ),
                statement.`in`
            ) as LIterable

            while (true) {
                try {
                    execute(statement.body)
                } catch (br: Break) {
                    return
                } catch (c: Continue) {
                    // Do nothing
                }

                if (!iterator.atEnd()) {
                    val distance = locals[statement.`in`]
                    if (distance != null) {
                        environment.assignAt(distance, statement.`in`.name, iterator.next())
                    } else {
                        globals.assign(statement.`in`.name, iterator.next())
                    }
                } else break
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitCallExpr(expr: Expr.Call, fileName: String?): Any? {
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

    override fun visitFunctionStatement(statement: Statement.Function, fileName: String?) {
        val function = LFunction(statement, environment)
        environment.define(statement.name.lexeme, function)
    }

    override fun visitReturnStatement(statement: Statement.Return, fileName: String?) {
        var value: Any? = null
        if (statement.value != null) value = evaluate(statement.value)

        throw Return(value)
    }

    override fun visitClassStatement(statement: Statement.Class, fileName: String?) {
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

        val staticMethods = mutableMapOf<String, LFunction>()
        for (method in statement.staticMethods) {
            val function = LFunction(method, environment, false)
            staticMethods[method.name.lexeme] = function
        }

        val klass = LClass(statement.name.lexeme, superClass as LClass?, methods, staticMethods)

        if (superClass != null) {
            environment = environment.enclosing!!
        }
        environment.assign(statement.name, klass)
    }

    override fun visitGetExpr(expr: Expr.Get, fileName: String?): Any? {
        val obj = evaluate(expr.obj)

        if (obj is LInstance) {
            return obj[expr.name]
        }

        if (obj is LClass) {
            return obj[expr.name]
        }

        throw RuntimeError(
            expr.name,
            "Only instances have properties."
        )
    }

    override fun visitSetExpr(expr: Expr.Set, fileName: String?): Any? {
        val obj = evaluate(expr.obj) as? LInstance ?: throw RuntimeError(
            expr.name,
            "Only instances have fields."
        )

        val value = evaluate(expr.value)
        obj.set(expr.name, value)
        return value
    }

    override fun visitSuperExpr(expr: Expr.Super, fileName: String?): Any {
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

    override fun visitAccessorExpr(expr: Expr.Accessor, fileName: String?): Any? {
        val variable = evaluate(expr.obj)
        val accessor = try {
            (evaluate(expr.accessor) as Double)
        } catch (ex: Exception) {
            throw RuntimeError(
                expr.accessorToken,
                "Wrong data type for accessor. Got ${evaluate(expr.accessor)} expected Number",
                fileName
            )
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

        if (variable is LCollection<*>) {
            return variable.at(accessor)
        }

        throw RuntimeError(expr.accessorToken, "Variable is not iterable.")
    }

    override fun visitAccessorSetExpr(expr: Expr.AccessorSet, fileName: String?): Any? {
        val variable = evaluate(expr.accessor.obj)
        val accessor = try {
            (evaluate(expr.accessor.accessor) as Double)
        } catch (ex: Exception) {
            throw RuntimeError(
                expr.accessor.accessorToken,
                "Wrong data type for accessor. Got ${evaluate(expr.accessor.accessor)} expected Number.",
                fileName
            )
        }

        val value = evaluate(expr.value)

        if (variable is LCollection<*>) {
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

    override fun visitThisExpr(expr: Expr.This, fileName: String?): Any? {
        return lookupVariable(expr.keyword, expr)
    }

    fun resolve(expr: Expr, depth: Int) {
        locals[expr] = depth
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun execute(statement: Statement, fileName: String? = null) {
        statement.accept(this, fileName)
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

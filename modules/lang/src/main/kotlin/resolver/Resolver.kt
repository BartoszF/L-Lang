package pl.bfelis.llang.language.resolver

import pl.bfelis.llang.language.LRuntime
import pl.bfelis.llang.language.ast.Expr
import pl.bfelis.llang.language.ast.Expr.Assign
import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.error.ResolverError
import pl.bfelis.llang.language.error.Warning
import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.scanner.Token
import pl.bfelis.llang.language.scanner.TokenType
import java.io.File
import java.util.*

data class IdentifierDefinition(val token: Token, val isVal: Boolean = false)

class Resolver(val interpreter: Interpreter, private val lRuntime: LRuntime) :
    Expr.Visitor<Unit>,
    Statement.Visitor<Unit> {
    private val identifiers: Stack<MutableMap<IdentifierDefinition, Int>> = Stack()
    private val scopes: Stack<MutableMap<String, Boolean>> = Stack()
    private val scripts: MutableList<String> = mutableListOf()
    private var currentFunction = FunctionType.NONE
    private var currentClass = ClassType.CLASS
    private val loopGuard = Stack<Statement>()

    override fun visitBlockStatement(statement: Statement.Block, fileName: String?) {
        resolve(statement.statements, true, fileName)
    }

    override fun visitBreakStatement(statement: Statement.Break, fileName: String?) {
        if (loopGuard.size == 0) {
            LRuntime.error(
                ResolverError(
                    statement.name,
                    "break not in loop",
                    fileName
                )
            )
        }
    }

    override fun visitContinueStatement(statement: Statement.Continue, fileName: String?) {
        if (loopGuard.size == 0) {
            LRuntime.error(
                ResolverError(
                    statement.name,
                    "continue not in loop",
                    fileName
                )
            )
        }
    }

    override fun visitVarStatement(statement: Statement.Var, fileName: String?) {
        declare(statement.name, fileName = fileName)
        if (statement.initializer != null) {
            resolve(statement.initializer, fileName)
        }
        define(statement.name)
    }

    override fun visitValStatement(statement: Statement.Val, fileName: String?) {
        declare(statement.name, true, fileName)
        if (statement.initializer != null) {
            resolve(statement.initializer, fileName)
        }
        define(statement.name)
    }

    override fun visitVariableExpr(expr: Expr.Variable, fileName: String?) {
        if (!scopes.isEmpty() &&
            scopes.peek()[expr.name.lexeme] === java.lang.Boolean.FALSE
        ) {
            LRuntime.error(ResolverError(expr.name, "Can't read local variable in its own initializer.", fileName))
        }
        resolveLocal(expr, expr.name)
    }

    override fun visitIncrementExpr(expr: Expr.Increment, fileName: String?) {
        resolveLocal(expr, expr.name)
    }

    override fun visitDecrementExpr(expr: Expr.Decrement, fileName: String?) {
        resolveLocal(expr, expr.name)
    }

    override fun visitAssignExpr(expr: Assign, fileName: String?) {
        resolve(expr.value, fileName)
        resolveLocal(expr, expr.name)

        val key = identifiers.indices.reversed()
            .firstNotNullOf { identifiers[it].filterKeys { k -> k.token.lexeme == expr.name.lexeme }.keys.firstOrNull() }

        if (key.isVal) {
            LRuntime.error(ResolverError(expr.name, "val cannot be reassigned.", fileName))
        }
    }

    override fun visitAssignIncrementExpr(expr: Expr.AssignIncrement, fileName: String?) {
        resolve(expr.value, fileName)
        resolveLocal(expr, expr.name)
    }

    override fun visitAssignDecrementExpr(expr: Expr.AssignDecrement, fileName: String?) {
        resolve(expr.value, fileName)
        resolveLocal(expr, expr.name)
    }

    override fun visitFunctionStatement(statement: Statement.Function, fileName: String?) {
        declare(statement.name, fileName = fileName)
        define(statement.name)
        resolveFunction(statement, FunctionType.FUNCTION, fileName)
    }

    override fun visitForStatement(statement: Statement.For, fileName: String?) {
        beginScope()
        loopGuard.add(statement)
        statement.initializer?.let { resolve(statement.initializer, fileName) }
        statement.condition?.let { resolve(statement.condition, fileName) }
        statement.step?.let { resolve(statement.step, fileName) }
        resolve(statement.body, fileName)
        loopGuard.pop()
        endScope()
    }

    override fun visitExpressionStatement(statement: Statement.Expression, fileName: String?) {
        resolve(statement.expression, fileName)
    }

    override fun visitIfStatement(statement: Statement.If, fileName: String?) {
        resolve(statement.condition, fileName)
        resolve(statement.thenBranch, fileName)
        if (statement.elseBranch != null) resolve(statement.elseBranch, fileName)
    }

    override fun visitImportStatement(statement: Statement.Import, fileName: String?) {
        val scriptName = statement.name.literal as String

        val parentFile = if (!fileName.isNullOrEmpty()) File(fileName) else null
        val parent = if (parentFile != null) {
            if (parentFile.isDirectory) parentFile.canonicalPath else parentFile.parent
        } else null

        val file = if (parent != null) File(parent, scriptName) else File(scriptName)

        if (!file.exists()) LRuntime.error(
            ResolverError(
                statement.name,
                "Script $scriptName does not exists.",
                fileName
            )
        )

        if (scripts.contains(file.canonicalPath)) return

        lRuntime.run(file, true)

        scripts.add(file.canonicalPath)
    }

    override fun visitReturnStatement(statement: Statement.Return, fileName: String?) {
        if (!arrayOf(FunctionType.FUNCTION, FunctionType.METHOD).contains(currentFunction)) {
            LRuntime.error(ResolverError(statement.keyword, "Can't return from top-level code.", fileName))
        }
        if (statement.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                LRuntime.error(ResolverError(statement.keyword, "Can't return a value from an initializer.", fileName))
            }
            resolve(statement.value, fileName)
        }
    }

    override fun visitWhileStatement(statement: Statement.While, fileName: String?) {
        loopGuard.add(statement)
        resolve(statement.condition, fileName)
        resolve(statement.body, fileName)
        loopGuard.pop()
    }

    override fun visitBinaryExpr(expr: Expr.Binary, fileName: String?) {
        resolve(expr.left, fileName)
        resolve(expr.right, fileName)
    }

    override fun visitCallExpr(expr: Expr.Call, fileName: String?) {
        resolve(expr.callee, fileName)
        for (argument in expr.arguments) {
            resolve(argument, fileName)
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping, fileName: String?) {
        resolve(expr.expression, fileName)
    }

    override fun visitLiteralExpr(expr: Expr.Literal, fileName: String?) {
    }

    override fun visitLogicalExpr(expr: Expr.Logical, fileName: String?) {
        resolve(expr.left, fileName)
        resolve(expr.right, fileName)
    }

    override fun visitLambdaExpr(expr: Expr.Lambda, fileName: String?) {
        resolveFunction(
            Statement.Function(Token(TokenType.FUN, "anonymous", "anonymous", expr.line), expr.params, expr.body),
            FunctionType.FUNCTION,
            fileName
        )
    }

    override fun visitUnaryExpr(expr: Expr.Unary, fileName: String?) {
        resolve(expr.right, fileName)
    }

    override fun visitClassStatement(statement: Statement.Class, fileName: String?) {
        val enclosingClass = currentClass
        currentClass = ClassType.CLASS
        declare(statement.name, fileName = fileName)
        define(statement.name)

        if (statement.superclass != null) {
            if (statement.superclass.name.lexeme == statement.name.lexeme) {
                LRuntime.error(ResolverError(statement.superclass.name, "A class can't inherit from itself.", fileName))
            }
            currentClass = ClassType.SUBCLASS
            resolve(statement.superclass, fileName)
        }

        if (statement.superclass != null) {
            beginScope()
            scopes.peek()["super"] = true
        }

        beginScope()
        scopes.peek()["this"] = true

        for (method in statement.methods) {
            var declaration = FunctionType.METHOD
            if (method.name.lexeme == "init") declaration = FunctionType.INITIALIZER
            resolveFunction(method, declaration, fileName)
        }

        for (method in statement.staticMethods) {
            val declaration = FunctionType.METHOD
            if (method.name.lexeme == "init") LRuntime.error(ResolverError(method.name, "Class cannot have static constructor.", fileName))
            resolveFunction(method, declaration, fileName)
        }

        endScope()

        if (statement.superclass != null) {
            endScope()
        }

        currentClass = enclosingClass
    }

    override fun visitSuperExpr(expr: Expr.Super, fileName: String?) {
        if (currentClass == ClassType.NONE) {
            LRuntime.error(
                ResolverError(
                    expr.keyword,
                    "Can't use 'super' outside of a class.",
                    fileName
                )
            )
        } else if (currentClass != ClassType.SUBCLASS) {
            LRuntime.error(
                ResolverError(
                    expr.keyword,
                    "Can't use 'super' in a class with no superclass.",
                    fileName
                )
            )
        }
        resolveLocal(expr, expr.keyword)
    }

    override fun visitAccessorExpr(expr: Expr.Accessor, fileName: String?) {
        resolve(expr.obj, fileName)
        resolve(expr.accessor, fileName)
    }

    override fun visitAccessorSetExpr(expr: Expr.AccessorSet, fileName: String?) {
        resolve(expr.accessor, fileName)
        resolve(expr.value, fileName)
    }

    override fun visitGetExpr(expr: Expr.Get, fileName: String?) {
        resolve(expr.obj, fileName)
    }

    override fun visitSetExpr(expr: Expr.Set, fileName: String?) {
        resolve(expr.value, fileName)
        resolve(expr.obj, fileName)
    }

    override fun visitThisExpr(expr: Expr.This, fileName: String?) {
        if (currentClass != ClassType.CLASS) {
            LRuntime.error(ResolverError(expr.keyword, "Can't use 'this' outside of class.", fileName))
        }
        resolveLocal(expr, expr.keyword)
    }

    fun resolve(statements: List<Statement?>, shouldCreateScope: Boolean = true, fileName: String?) {
        if (shouldCreateScope) {
            beginScope()
        }
        for (statement in statements) {
            resolve(statement, fileName)
        }
        if (shouldCreateScope) {
            checkUnusedVariables(fileName)
            endScope()
        }
    }

    private fun resolveLocal(expr: Expr, name: Token) {
        for (i in scopes.indices.reversed()) {
            if (scopes[i].containsKey(name.lexeme)) {
                if (identifiers[i].isNotEmpty()) {
                    val key = identifiers[i].filterKeys { it.token.lexeme == name.lexeme }.keys.first()
                    identifiers[i][key] = identifiers[i][key]!! - 1
                }
                interpreter.resolve(expr, scopes.size - 1 - i)
                return
            }
        }
    }

    private fun resolveFunction(function: Statement.Function, functionType: FunctionType, fileName: String?) {
        val enclosingFunction = currentFunction
        currentFunction = functionType
        beginScope()
        for (param in function.params) {
            declare(param, fileName = fileName)
            define(param)
        }
        resolve(function.body, false, fileName)
        checkUnusedVariables(fileName)
        endScope()
        currentFunction = enclosingFunction
    }

    private fun declare(name: Token, isVal: Boolean = false, fileName: String? = null) {
        if (scopes.isEmpty() && identifiers.isEmpty()) return

        val scope: MutableMap<String, Boolean> = scopes.peek()
        val block: MutableMap<IdentifierDefinition, Int> = identifiers.peek()
        if (scope.containsKey(name.lexeme)) {
            LRuntime.error(ResolverError(name, "Already a variable with this name in this scope", fileName))
        }

        scope[name.lexeme] = false
        block[IdentifierDefinition(name, isVal)] = 0
    }

    private fun define(name: Token) {
        if (scopes.isEmpty() && identifiers.isEmpty()) return

        scopes.peek()[name.lexeme] = true
    }

    private fun resolve(stmt: Statement?, fileName: String? = null) {
        stmt?.accept(this, fileName)
    }

    private fun resolve(expr: Expr, fileName: String? = null) {
        expr.accept(this, fileName)
    }

    private fun beginScope() {
        scopes.push(mutableMapOf())
        identifiers.push(mutableMapOf())
    }

    private fun endScope() {
        scopes.pop()
        identifiers.pop()
    }

    private fun checkUnusedVariables(fileName: String?) {
        val block = identifiers.peek()
        for ((definition, usage) in block) {
            if (usage == 0) {
                LRuntime.warn(Warning(definition.token, "Unused variable.", fileName))
            }
        }
    }
}

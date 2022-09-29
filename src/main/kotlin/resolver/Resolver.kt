package pl.bfelis.fc93.language.resolver

import pl.bfelis.fc93.language.Language
import pl.bfelis.fc93.language.ast.Expr
import pl.bfelis.fc93.language.ast.Expr.Assign
import pl.bfelis.fc93.language.ast.Statement
import pl.bfelis.fc93.language.interpreter.Interpreter
import pl.bfelis.fc93.language.scanner.Token
import java.util.*

class Resolver(val interpreter: Interpreter) : Expr.Visitor<Unit>, Statement.Visitor<Unit> {
    private val identifiers: Stack<MutableMap<Token, Int>> = Stack()
    private val scopes: Stack<MutableMap<String, Boolean>> = Stack()
    private var currentFunction = FunctionType.NONE
    private var currentClass = ClassType.CLASS

    override fun visitBlockStatement(statement: Statement.Block) {
        beginScope()
        resolve(statement.statements)
        endScope()
    }

    override fun visitVarStatement(statement: Statement.Var) {
        declare(statement.name)
        if (statement.initializer != null) {
            resolve(statement.initializer)
        }
        define(statement.name)
    }

    override fun visitVariableExpr(expr: Expr.Variable) {
        if (!scopes.isEmpty() &&
            scopes.peek()[expr.name.lexeme] === java.lang.Boolean.FALSE
        ) {
            Language.error(expr.name, "Can't read local variable in its own initializer.")
        }
        resolveLocal(expr, expr.name)
    }

    override fun visitAssignExpr(expr: Assign) {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
    }

    override fun visitFunctionStatement(statement: Statement.Function) {
        declare(statement.name)
        define(statement.name)
        resolveFunction(statement, FunctionType.FUNCTION)
    }

    override fun visitExpressionStatement(statement: Statement.Expression) {
        resolve(statement.expression)
    }

    override fun visitIfStatement(statement: Statement.If) {
        resolve(statement.condition)
        resolve(statement.thenBranch)
        if (statement.elseBranch != null) resolve(statement.elseBranch)
    }

    override fun visitPrintStatement(statement: Statement.Print) {
        resolve(statement.expression)
    }

    override fun visitReturnStatement(statement: Statement.Return) {
        if (currentFunction != FunctionType.FUNCTION) {
            Language.error(statement.keyword, "Can't return from top-level code.")
        }
        if (statement.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Language.error(statement.keyword, "Can't return a value from an initializer.")
            }
            resolve(statement.value)
        }
    }

    override fun visitWhileStatement(statement: Statement.While) {
        resolve(statement.condition)
        resolve(statement.body)
    }

    override fun visitBinaryExpr(expr: Expr.Binary) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visitCallExpr(expr: Expr.Call) {
        resolve(expr.callee)
        for (argument in expr.arguments) {
            resolve(argument)
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping) {
        resolve(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal) {
    }

    override fun visitLogicalExpr(expr: Expr.Logical) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visitUnaryExpr(expr: Expr.Unary) {
        resolve(expr.right)
    }

    override fun visitClassStatement(statement: Statement.Class) {
        val enclosingClass = currentClass
        currentClass = ClassType.CLASS
        declare(statement.name)
        define(statement.name)

        if (statement.superclass != null) {
            if (statement.superclass.name.lexeme == statement.name.lexeme) {
                Language.error(statement.superclass.name, "A class can't inherit from itself.")
            }
            currentClass = ClassType.SUBCLASS
            resolve(statement.superclass)
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
            resolveFunction(method, declaration)
        }

        endScope()

        if (statement.superclass != null) {
            endScope()
        }

        currentClass = enclosingClass
    }

    override fun visitSuperExpr(expr: Expr.Super) {
        if (currentClass == ClassType.NONE) {
            Language.error(
                expr.keyword,
                "Can't use 'super' outside of a class."
            )
        } else if (currentClass != ClassType.SUBCLASS) {
            Language.error(
                expr.keyword,
                "Can't use 'super' in a class with no superclass."
            )
        }
        resolveLocal(expr, expr.keyword)
    }

    override fun visitGetExpr(expr: Expr.Get) {
        resolve(expr.obj)
    }

    override fun visitSetExpr(expr: Expr.Set) {
        resolve(expr.value)
        resolve(expr.obj)
    }

    override fun visitThisExpr(expr: Expr.This) {
        if (currentClass != ClassType.CLASS) {
            Language.error(expr.keyword, "Can't use 'this' outside of class.")
        }
        resolveLocal(expr, expr.keyword)
    }

    fun resolve(statements: List<Statement?>) {
        for (statement in statements) {
            resolve(statement)
        }
    }

    private fun resolveLocal(expr: Expr, name: Token) {
        for (i in scopes.indices.reversed()) {
            if (scopes[i].containsKey(name.lexeme)) {
                identifiers[i].remove(name)
                interpreter.resolve(expr, scopes.size - 1 - i)
                return
            }
        }
    }

    private fun resolveFunction(function: Statement.Function, functionType: FunctionType) {
        val enclosingFunction = currentFunction
        currentFunction = functionType
        beginScope()
        for (param in function.params) {
            declare(param)
            define(param)
        }
        resolve(function.body)
        checkUnusedVariables()
        endScope()
        currentFunction = enclosingFunction
    }

    private fun declare(name: Token) {
        if (scopes.isEmpty() && identifiers.isEmpty()) return

        val scope: MutableMap<String, Boolean> = scopes.peek()
        val block: MutableMap<Token, Int> = identifiers.peek()
        if (scope.containsKey(name.lexeme)) {
            Language.error(name, "Already a variable with this name in this scope")
        }
        scope[name.lexeme] = false
        block[name] = 0
    }

    private fun define(name: Token) {
        if (scopes.isEmpty() && identifiers.isEmpty()) return
        scopes.peek()[name.lexeme] = true
    }

    private fun resolve(stmt: Statement?) {
        stmt?.accept(this)
    }

    private fun resolve(expr: Expr) {
        expr.accept(this)
    }

    private fun beginScope() {
        scopes.push(mutableMapOf())
        identifiers.push(mutableMapOf())
    }

    private fun endScope() {
        scopes.pop()
        identifiers.pop()
    }

    private fun checkUnusedVariables() {
        val block = identifiers.peek()
        for ((name, usage) in block) {
            Language.warn(name, "Unused variable.")
        }
    }
}

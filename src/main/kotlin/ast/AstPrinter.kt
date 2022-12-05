package pl.bfelis.fc93.language.ast

class AstPrinter : Expr.Visitor<String>, Statement.Visitor<String> {

    var indent = -1

    private fun currentIndent(plus: Int? = 0): String {
        return "\t".repeat(indent + (plus ?: 0))
    }

    fun printExpr(expr: Expr): String {
        return expr.accept(this)
    }

    fun printStatement(statement: Statement?): String {
        return statement?.accept(this) ?: ""
    }

    fun printStatements(statements: List<Statement?>): String {
        indent++
        val res = statements.joinToString(System.lineSeparator()) { currentIndent() + printStatement(it) }
        indent--
        return res
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        return "(Literal ${if (expr.value == null) "nil" else expr.value.toString()})"
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitVariableExpr(expr: Expr.Variable): String {
        return "(Variable ${expr.name.lexeme})"
    }

    override fun visitIncrementExpr(expr: Expr.Increment): String {
        return "(Increment ${expr.name.lexeme})"
    }

    override fun visitDecrementExpr(expr: Expr.Decrement): String {
        return "(Decrement ${expr.name.lexeme})"
    }

    override fun visitAssignExpr(expr: Expr.Assign): String {
        return "(Assign ${expr.name.lexeme} = ${expr.value.accept(this)})"
    }

    override fun visitAssignIncrementExpr(expr: Expr.AssignIncrement): String {
        return "(Assign ${expr.name.lexeme} += ${expr.value.accept(this)}"
    }

    override fun visitAssignDecrementExpr(expr: Expr.AssignDecrement): String {
        return "(Assign ${expr.name.lexeme} -= ${expr.value.accept(this)}"
    }

    override fun visitLogicalExpr(expr: Expr.Logical): String {
        return "(Logical ${parenthesize(expr.operator.lexeme, expr.left, expr.right)})"
    }

    override fun visitLambdaExpr(expr: Expr.Lambda): String {
        return """(Lambda params: ${expr.params}
            |${currentIndent(1)}${printStatements(expr.body)}
            |)
        """.trimMargin()
    }

    override fun visitCallExpr(expr: Expr.Call): String {
        return parenthesize("Call", expr.callee, *expr.arguments.toTypedArray())
    }

    override fun visitGetExpr(expr: Expr.Get): String {
        return "(Getter ${expr.obj.accept(this)}.${expr.name.lexeme})"
    }

    override fun visitSetExpr(expr: Expr.Set): String {
        return "(Setter ${expr.obj.accept(this)}.${expr.name.lexeme} = ${printExpr(expr.value)})"
    }

    override fun visitThisExpr(expr: Expr.This): String {
        return "(${expr.keyword.lexeme})"
    }

    override fun visitSuperExpr(expr: Expr.Super): String {
        return "(super ${expr.method.lexeme} ${expr.keyword.lexeme})"
    }

    override fun visitAccessorExpr(expr: Expr.Accessor): String {
        return "(accessor ${expr.obj.accept(this)} [${expr.accessor.accept(this)}]}"
    }

    override fun visitAccessorSetExpr(expr: Expr.AccessorSet): String {
        return parenthesize("accessorSet", expr.accessor, expr.value)
    }

    override fun visitBlockStatement(statement: Statement.Block): String {
        return """(Block 
            |${printStatements(statement.statements)}
            |${currentIndent()})
        """.trimMargin()
    }

    override fun visitClassStatement(statement: Statement.Class): String {
        return """(Class 
            |${currentIndent(1)}(name ${statement.name.lexeme}):${statement.superclass?.accept(this) ?: "no superclass"} 
            |${printStatements(statement.methods)}
            |${currentIndent()})
        """.trimMargin()
    }

    override fun visitExpressionStatement(statement: Statement.Expression): String {
        return parenthesize("expression", statement.expression)
    }

    override fun visitFunctionStatement(statement: Statement.Function): String {
        return """(Function ${statement.name.lexeme}
            |${currentIndent(1)}(params [${statement.params.joinToString(", ")}]) 
            |${printStatements(statement.body)}
            |${currentIndent()})
        """.trimMargin()
    }

    override fun visitIfStatement(statement: Statement.If): String {
        return """(If ${statement.condition.accept(this)}
            |${currentIndent(1)}${statement.thenBranch.accept(this)} 
            |${currentIndent(1)}(ELSE 
            |${currentIndent(1)}${statement.elseBranch?.accept(this)}
            |${currentIndent()})
        """.trimIndent()
    }

    override fun visitPrintStatement(statement: Statement.Print): String {
        return "(Print ${statement.expression.accept(this)})"
    }

    override fun visitReturnStatement(statement: Statement.Return): String {
        return "(Return ${statement.keyword.lexeme} ${statement.value?.accept(this)})"
    }

    override fun visitVarStatement(statement: Statement.Var): String {
        return "(Var ${statement.name.lexeme} = ${statement.initializer?.accept(this) ?: "nil"})"
    }

    override fun visitValStatement(statement: Statement.Val): String {
        return "(Val ${statement.name.lexeme} = ${statement.initializer?.accept(this) ?: "nil"})"
    }

    override fun visitWhileStatement(statement: Statement.While): String {
        return """(While 
            |${currentIndent(1)}${parenthesize("condition", statement.condition)}
            |${currentIndent(1)}${statement.body.accept(this)}
            |${currentIndent()})
        """.trimMargin()
    }
}

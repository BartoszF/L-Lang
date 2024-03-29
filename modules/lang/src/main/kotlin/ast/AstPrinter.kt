package pl.bfelis.llang.language.ast

class AstPrinter : Expr.Visitor<String>, Statement.Visitor<String> {

    var indent = -1

    private fun currentIndent(plus: Int? = 0): String {
        return "\t".repeat(indent + (plus ?: 0))
    }

    private fun printExpr(expr: Expr): String {
        return expr.accept(this)
    }

    private fun printStatement(statement: Statement?): String {
        return statement?.accept(this) ?: ""
    }

    fun printStatements(statements: List<Statement?>): String {
        indent++
        val res = statements.joinToString(System.lineSeparator()) { currentIndent() + printStatement(it) }
        indent--
        return res
    }

    override fun visitBinaryExpr(expr: Expr.Binary, fileName: String?): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping, fileName: String?): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal, fileName: String?): String {
        return "(Literal ${if (expr.value == null) "nil" else expr.value.toString()})"
    }

    override fun visitListDefExpr(expr: Expr.ListDef, fileName: String?): String {
        return "(List ${expr.elements.map { it.accept(this) }})"
    }

    override fun visitUnaryExpr(expr: Expr.Unary, fileName: String?): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    private fun parenthesize(name: String, vararg exprs: Expr?): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            if (expr != null) {
                builder.append(expr.accept(this))
            } else {
                builder.append("nil")
            }
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitVariableExpr(expr: Expr.Variable, fileName: String?): String {
        return "(Variable ${expr.name.lexeme})"
    }

    override fun visitIncrementExpr(expr: Expr.Increment, fileName: String?): String {
        return "(Increment ${expr.name.lexeme})"
    }

    override fun visitDecrementExpr(expr: Expr.Decrement, fileName: String?): String {
        return "(Decrement ${expr.name.lexeme})"
    }

    override fun visitAssignExpr(expr: Expr.Assign, fileName: String?): String {
        return "(Assign ${expr.name.lexeme} = ${expr.value.accept(this)})"
    }

    override fun visitAssignIncrementExpr(expr: Expr.AssignIncrement, fileName: String?): String {
        return "(Assign ${expr.name.lexeme} += ${expr.value.accept(this)}"
    }

    override fun visitAssignDecrementExpr(expr: Expr.AssignDecrement, fileName: String?): String {
        return "(Assign ${expr.name.lexeme} -= ${expr.value.accept(this)}"
    }

    override fun visitLogicalExpr(expr: Expr.Logical, fileName: String?): String {
        return "(Logical ${parenthesize(expr.operator.lexeme, expr.left, expr.right)})"
    }

    override fun visitLambdaExpr(expr: Expr.Lambda, fileName: String?): String {
        return """(Lambda
            |${currentIndent(1)}(params [${expr.params.joinToString(", ")}]) 
            |${printStatements(expr.body)}
            |)
        """.trimMargin()
    }

    override fun visitSliceExpr(expr: Expr.Slice, fileName: String?): String {
        return "(Slice ${expr.obj.accept(this)} ${parenthesize("start", expr.start)} ${
        parenthesize(
            "count",
            expr.count
        )
        }}"
    }

    override fun visitCallExpr(expr: Expr.Call, fileName: String?): String {
        return parenthesize("Call", expr.callee, *expr.arguments.toTypedArray())
    }

    override fun visitGetExpr(expr: Expr.Get, fileName: String?): String {
        return "(Getter ${expr.obj.accept(this)}.${expr.name.lexeme})"
    }

    override fun visitSetExpr(expr: Expr.Set, fileName: String?): String {
        return "(Setter ${expr.obj.accept(this)}.${expr.name.lexeme} = ${printExpr(expr.value)})"
    }

    override fun visitThisExpr(expr: Expr.This, fileName: String?): String {
        return "(${expr.keyword.lexeme})"
    }

    override fun visitSuperExpr(expr: Expr.Super, fileName: String?): String {
        return "(super ${expr.method.lexeme} ${expr.keyword.lexeme})"
    }

    override fun visitAccessorExpr(expr: Expr.Accessor, fileName: String?): String {
        return "(accessor ${expr.obj.accept(this)} [${expr.accessor.accept(this)}]"
    }

    override fun visitAccessorSetExpr(expr: Expr.AccessorSet, fileName: String?): String {
        return parenthesize("accessorSet", expr.accessor, expr.value)
    }

    override fun visitInExpr(expr: Expr.In, fileName: String?): String {
        return "(in ${expr.name} ${expr.iterable.accept(this)})"
    }

    override fun visitBlockStatement(statement: Statement.Block, fileName: String?): String {
        return """(Block 
            |${printStatements(statement.statements)}
            |${currentIndent()})
        """.trimMargin()
    }

    override fun visitBreakStatement(statement: Statement.Break, fileName: String?): String {
        return "(Break)"
    }

    override fun visitContinueStatement(statement: Statement.Continue, fileName: String?): String {
        return "(Continue)"
    }

    override fun visitClassStatement(statement: Statement.Class, fileName: String?): String {
        return """(Class 
            |${currentIndent(1)}(name ${statement.name.lexeme}):${statement.superclass?.accept(this) ?: "no superclass"} 
            |${printStatements(statement.methods)}
            |${printStatements(statement.staticMethods)}
            |${currentIndent()})
        """.trimMargin()
    }

    override fun visitExpressionStatement(statement: Statement.Expression, fileName: String?): String {
        return parenthesize("expression", statement.expression)
    }

    override fun visitFunctionStatement(statement: Statement.Function, fileName: String?): String {
        return """(Function ${if (statement.isStatic) "static" else ""}${statement.name.lexeme}
            |${currentIndent(1)}(params [${statement.params.joinToString(", ")}]) 
            |${printStatements(statement.body)}
            |${currentIndent()})
        """.trimMargin()
    }

    override fun visitForStatement(statement: Statement.For, fileName: String?): String {
        return """(For 
            |${currentIndent(1)}${statement.`in`.accept(this)}
            |${currentIndent(1)}${statement.body.accept(this)}
            |${currentIndent()})
        """.trimIndent()
    }

    override fun visitIfStatement(statement: Statement.If, fileName: String?): String {
        return """(If ${statement.condition.accept(this)}
            |${currentIndent(1)}${statement.thenBranch.accept(this)} 
            |${currentIndent()}(ELSE 
            |${currentIndent(1)}${statement.elseBranch?.accept(this)}
            |${currentIndent()})
        """.trimIndent()
    }

    override fun visitImportStatement(statement: Statement.Import, fileName: String?): String {
        return "(Import ${statement.name.lexeme})"
    }

    override fun visitReturnStatement(statement: Statement.Return, fileName: String?): String {
        return "(Return ${statement.keyword.lexeme} ${statement.value?.accept(this)})"
    }

    override fun visitVarStatement(statement: Statement.Var, fileName: String?): String {
        return "(Var ${statement.name.lexeme} = ${statement.initializer?.accept(this) ?: "nil"})"
    }

    override fun visitValStatement(statement: Statement.Val, fileName: String?): String {
        return "(Val ${statement.name.lexeme} = ${statement.initializer?.accept(this) ?: "nil"})"
    }

    override fun visitListSpreadStatement(statement: Statement.ListSpread, fileName: String?): String {
        return "(ListSpread ${statement.names.map { it.lexeme }} = ${statement.initializer.accept(this)})"
    }

    override fun visitWhileStatement(statement: Statement.While, fileName: String?): String {
        return """(While 
            |${currentIndent(1)}${parenthesize("condition", statement.condition)}
            |${currentIndent(1)}${statement.body.accept(this)}
            |${currentIndent()})
        """.trimMargin()
    }
}

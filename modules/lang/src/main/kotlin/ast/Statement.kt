package pl.bfelis.llang.language.ast

import pl.bfelis.llang.language.scanner.Token

abstract class Statement {
    interface Visitor<R> {
        fun visitBlockStatement(statement: Block, fileName: String? = null): R
        fun visitBreakStatement(statement: Break, fileName: String? = null): R
        fun visitContinueStatement(statement: Continue, fileName: String? = null): R
        fun visitClassStatement(statement: Class, fileName: String? = null): R
        fun visitExpressionStatement(statement: Expression, fileName: String? = null): R
        fun visitFunctionStatement(statement: Function, fileName: String? = null): R
        fun visitForStatement(statement: For, fileName: String? = null): R
        fun visitIfStatement(statement: If, fileName: String? = null): R
        fun visitImportStatement(statement: Import, fileName: String? = null): R
        fun visitReturnStatement(statement: Return, fileName: String? = null): R
        fun visitVarStatement(statement: Var, fileName: String? = null): R
        fun visitValStatement(statement: Val, fileName: String? = null): R
        fun visitListSpreadStatement(statement: ListSpread, fileName: String? = null): R
        fun visitWhileStatement(statement: While, fileName: String? = null): R
    }

    data class Block(
        val statements: List<Statement?>
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitBlockStatement(this, fileName)
        }
    }

    data class Break(
        val name: Token
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitBreakStatement(this, fileName)
        }
    }

    data class Continue(
        val name: Token
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitContinueStatement(this, fileName)
        }
    }

    data class Class(
        val name: Token,
        val superclass: Expr.Variable?,
        val methods: List<Statement.Function>,
        val staticMethods: List<Statement.Function>
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitClassStatement(this, fileName)
        }
    }

    data class Expression(
        val expression: Expr
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitExpressionStatement(this, fileName)
        }
    }

    data class Function(
        val name: Token,
        val params: List<Token>,
        val body: List<Statement?>,
        val isStatic: Boolean
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitFunctionStatement(this, fileName)
        }
    }

    data class For(
        val `in`: Expr.In,
        val body: Statement
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitForStatement(this, fileName)
        }
    }

    data class If(
        val condition: Expr,
        val thenBranch: Statement,
        val elseBranch: Statement?
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitIfStatement(this, fileName)
        }
    }

    data class Import(
        val name: Token
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitImportStatement(this, fileName)
        }
    }

    data class Return(
        val keyword: Token,
        val value: Expr?
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitReturnStatement(this, fileName)
        }
    }

    data class Var(
        val name: Token,
        val initializer: Expr?
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitVarStatement(this, fileName)
        }
    }

    data class Val(
        val name: Token,
        val initializer: Expr?
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitValStatement(this, fileName)
        }
    }

    data class ListSpread(
        val names: List<Token>,
        val initializer: Expr,
        val isVal: Boolean
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitListSpreadStatement(this, fileName)
        }
    }

    data class While(
        val condition: Expr,
        val body: Statement
    ) : Statement() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitWhileStatement(this, fileName)
        }
    }

    abstract fun <R> accept(visitor: Visitor<R>, fileName: String? = null): R
}

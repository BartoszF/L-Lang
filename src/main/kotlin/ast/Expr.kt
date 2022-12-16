package pl.bfelis.llang.language.ast

import pl.bfelis.llang.language.scanner.Token

abstract class Expr {
    interface Visitor<R> {
        fun visitAssignExpr(expr: Assign): R
        fun visitAssignIncrementExpr(expr: AssignIncrement): R
        fun visitAssignDecrementExpr(expr: AssignDecrement): R
        fun visitBinaryExpr(expr: Binary): R
        fun visitCallExpr(expr: Call): R
        fun visitGetExpr(expr: Get): R
        fun visitSetExpr(expr: Set): R
        fun visitSuperExpr(expr: Super): R
        fun visitAccessorExpr(expr: Accessor): R
        fun visitAccessorSetExpr(expr: AccessorSet): R
        fun visitThisExpr(expr: This): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitLogicalExpr(expr: Logical): R
        fun visitLambdaExpr(expr: Lambda): R
        fun visitUnaryExpr(expr: Unary): R
        fun visitVariableExpr(expr: Variable): R
        fun visitIncrementExpr(expr: Increment): R
        fun visitDecrementExpr(expr: Decrement): R
    }

    class Assign(
        val name: Token,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitAssignExpr(this)
        }
    }

    class AssignIncrement(
        val name: Token,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitAssignIncrementExpr(this)
        }
    }

    class AssignDecrement(
        val name: Token,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitAssignDecrementExpr(this)
        }
    }

    class Binary(
        val left: Expr,
        val operator: Token,
        val right: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitBinaryExpr(this)
        }
    }

    class Call(
        val callee: Expr,
        val paren: Token,
        val arguments: List<Expr>
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitCallExpr(this)
        }
    }

    class Get(
        val obj: Expr,
        val name: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitGetExpr(this)
        }
    }

    class Set(
        val obj: Expr,
        val name: Token,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitSetExpr(this)
        }
    }

    class Super(
        val keyword: Token,
        val method: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitSuperExpr(this)
        }
    }

    class Accessor(
        val obj: Expr,
        val accessor: Expr,
        val accessorToken: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitAccessorExpr(this)
        }
    }

    class AccessorSet(
        val accessor: Expr.Accessor,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitAccessorSetExpr(this)
        }
    }

    class This(
        val keyword: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitThisExpr(this)
        }
    }

    class Grouping(
        val expression: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitGroupingExpr(this)
        }
    }

    class Literal(
        val value: Any?
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitLiteralExpr(this)
        }
    }

    class Logical(
        val left: Expr,
        val operator: Token,
        val right: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitLogicalExpr(this)
        }
    }

    class Lambda(
        val line: Int,
        val params: List<Token>,
        val body: List<Statement?>
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitLambdaExpr(this)
        }
    }

    class Unary(
        val operator: Token,
        val right: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitUnaryExpr(this)
        }
    }

    class Variable(
        val name: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitVariableExpr(this)
        }
    }

    class Increment(
        val name: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitIncrementExpr(this)
        }
    }

    class Decrement(
        val name: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitDecrementExpr(this)
        }
    }

    abstract fun <R> accept(visitor: Visitor<R>): R
}

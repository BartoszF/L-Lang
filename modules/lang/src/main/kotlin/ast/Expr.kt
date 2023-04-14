package pl.bfelis.llang.language.ast

import pl.bfelis.llang.language.scanner.Token

abstract class Expr {
    interface Visitor<R> {
        fun visitAssignExpr(expr: Assign, fileName: String? = null): R
        fun visitAssignIncrementExpr(expr: AssignIncrement, fileName: String? = null): R
        fun visitAssignDecrementExpr(expr: AssignDecrement, fileName: String? = null): R
        fun visitBinaryExpr(expr: Binary, fileName: String? = null): R
        fun visitCallExpr(expr: Call, fileName: String? = null): R
        fun visitGetExpr(expr: Get, fileName: String? = null): R
        fun visitInExpr(expr: In, fileName: String? = null): R
        fun visitSetExpr(expr: Set, fileName: String? = null): R
        fun visitSuperExpr(expr: Super, fileName: String? = null): R
        fun visitAccessorExpr(expr: Accessor, fileName: String? = null): R
        fun visitAccessorSetExpr(expr: AccessorSet, fileName: String? = null): R
        fun visitThisExpr(expr: This, fileName: String? = null): R
        fun visitGroupingExpr(expr: Grouping, fileName: String? = null): R
        fun visitLiteralExpr(expr: Literal, fileName: String? = null): R
        fun visitListDefExpr(expr: ListDef, fileName: String? = null): R
        fun visitLogicalExpr(expr: Logical, fileName: String? = null): R
        fun visitLambdaExpr(expr: Lambda, fileName: String? = null): R
        fun visitSliceExpr(expr: Slice, fileName: String? = null): R
        fun visitUnaryExpr(expr: Unary, fileName: String? = null): R
        fun visitVariableExpr(expr: Variable, fileName: String? = null): R
        fun visitIncrementExpr(expr: Increment, fileName: String? = null): R
        fun visitDecrementExpr(expr: Decrement, fileName: String? = null): R
    }

    class Assign(
        val name: Token,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitAssignExpr(this, fileName)
        }
    }

    class AssignIncrement(
        val name: Token,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitAssignIncrementExpr(this, fileName)
        }
    }

    class AssignDecrement(
        val name: Token,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitAssignDecrementExpr(this, fileName)
        }
    }

    class Binary(
        val left: Expr,
        val operator: Token,
        val right: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitBinaryExpr(this, fileName)
        }
    }

    class Call(
        val callee: Expr,
        val paren: Token,
        val arguments: List<Expr>
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitCallExpr(this, fileName)
        }
    }

    class Get(
        val obj: Expr,
        val name: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitGetExpr(this, fileName)
        }
    }

    class In(
        val name: Token,
        val iterable: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitInExpr(this, fileName)
        }
    }

    class Set(
        val obj: Expr,
        val name: Token,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitSetExpr(this, fileName)
        }
    }

    class Super(
        val keyword: Token,
        val method: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitSuperExpr(this, fileName)
        }
    }

    class Accessor(
        val obj: Expr,
        val accessor: Expr,
        val accessorToken: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitAccessorExpr(this, fileName)
        }
    }

    class AccessorSet(
        val accessor: Expr.Accessor,
        val value: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitAccessorSetExpr(this, fileName)
        }
    }

    class This(
        val keyword: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitThisExpr(this, fileName)
        }
    }

    class Grouping(
        val expression: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitGroupingExpr(this, fileName)
        }
    }

    class Literal(
        val value: Any?
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitLiteralExpr(this, fileName)
        }
    }

    class ListDef(
        val elements: List<Expr>
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitListDefExpr(this, fileName)
        }
    }

    class Logical(
        val left: Expr,
        val operator: Token,
        val right: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitLogicalExpr(this, fileName)
        }
    }

    class Lambda(
        val line: Int,
        val params: List<Token>,
        val body: List<Statement?>
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitLambdaExpr(this, fileName)
        }
    }

    class Slice(
        val token: Token,
        val obj: Expr,
        val start: Expr?,
        val count: Expr?
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitSliceExpr(this, fileName)
        }
    }

    class Unary(
        val operator: Token,
        val right: Expr
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitUnaryExpr(this, fileName)
        }
    }

    class Variable(
        val name: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitVariableExpr(this, fileName)
        }
    }

    class Increment(
        val name: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitIncrementExpr(this, fileName)
        }
    }

    class Decrement(
        val name: Token
    ) : Expr() {

        override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {
            return visitor.visitDecrementExpr(this, fileName)
        }
    }

    abstract fun <R> accept(visitor: Visitor<R>, fileName: String? = null): R
}

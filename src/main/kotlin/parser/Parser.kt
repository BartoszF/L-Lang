package pl.bfelis.fc93.language.parser

import pl.bfelis.fc93.language.LRuntime
import pl.bfelis.fc93.language.ast.Expr
import pl.bfelis.fc93.language.ast.Expr.Assign
import pl.bfelis.fc93.language.ast.Expr.Logical
import pl.bfelis.fc93.language.ast.Statement
import pl.bfelis.fc93.language.error.ParserError
import pl.bfelis.fc93.language.scanner.Token
import pl.bfelis.fc93.language.scanner.TokenType

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): List<Statement?> {
        val statements = mutableListOf<Statement?>()

        while (!isAtEnd()) {
            try {
                statements.add(declaration())
            } catch (e: ParserError) {
                LRuntime.error(e)
            }
        }

        return statements
    }

    private fun declaration(): Statement? {
        try {
            if (match(TokenType.CLASS)) return classDeclaration()
            if (match(TokenType.FUN)) return function("function")
            if (match(TokenType.VAR)) return varDeclaration()
            if (match(TokenType.VAL)) return valDeclaration()

            return statement()
        } catch (e: ParserError) {
            synchronize()
            return null
        }
    }

    private fun classDeclaration(): Statement {
        val name = consume(TokenType.IDENTIFIER, "Expect class name.")

        var superclass: Expr.Variable? = null
        if (match(TokenType.COLON)) {
            consume(TokenType.IDENTIFIER, "Expect superclass name.")
            superclass = Expr.Variable(previous())
        }

        consume(TokenType.LEFT_BRACE, "Expect '{' before class body.")

        val methods: MutableList<Statement.Function> = mutableListOf()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"))
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.")

        return Statement.Class(name, superclass, methods)
    }

    private fun function(kind: String): Statement.Function {
        val name = consume(TokenType.IDENTIFIER, "Expect $kind name.")
        consume(TokenType.LEFT_PAREN, "Expect '(' after $kind name.")
        val parameters: MutableList<Token> = ArrayList()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) {
                    LRuntime.error(ParserError(peek(), "Can't have more than 255 parameters."))
                }
                parameters.add(
                    consume(TokenType.IDENTIFIER, "Expect parameter name.")
                )
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
        consume(TokenType.LEFT_BRACE, "Expect '{' before $kind body.")
        val body: List<Statement?> = block()
        return Statement.Function(name, parameters, body)
    }

    private fun varDeclaration(): Statement {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")
        var initializer: Expr? = null
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        }

        return Statement.Var(name, initializer)
    }

    private fun valDeclaration(): Statement {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")
        val initializer: Expr
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        } else {
            throw ParserError(name, "val needs initializer.")
        }

        return Statement.Val(name, initializer)
    }

    private fun statement(): Statement {
        if (match(TokenType.FOR)) return forStatement()
        if (match(TokenType.IF)) return ifStatement()
        if (match(TokenType.PRINT)) return printStatement()
        if (match(TokenType.RETURN)) return returnStatement()
        if (match(TokenType.WHILE)) return whileStatement()
        if (match(TokenType.LEFT_BRACE)) return Statement.Block(block())

        return expressionStatement()
    }

    private fun forStatement(): Statement {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.")
        val initializer = if (match(TokenType.SEMICOLON)) null
        else if (match(TokenType.VAR)) {
            varDeclaration()
        } else {
            expressionStatement()
        }

        if (initializer != null) {
            consume(TokenType.SEMICOLON, "Expect ';' after initializer.")
        }

        var condition: Expr? = null
        if (!check(TokenType.SEMICOLON)) {
            condition = expression()
        }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.")

        var increment: Expr? = null
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression()
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.")

        var body = statement()

        if (increment != null) {
            body = Statement.Block(
                listOf(body, Statement.Expression(increment))
            )
        }

        if (condition == null) condition = Expr.Literal(true)
        body = Statement.While(condition, body)

        if (initializer != null) body = Statement.Block(listOf(initializer, body))

        return body
    }

    private fun ifStatement(): Statement {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.")
        val thenBranch: Statement = statement()
        var elseBranch: Statement? = null
        if (match(TokenType.ELSE)) {
            elseBranch = statement()
        }
        return Statement.If(condition, thenBranch, elseBranch)
    }

    private fun printStatement(): Statement {
        val value = expression()
        return Statement.Print(value)
    }

    private fun returnStatement(): Statement {
        val keyword = previous()
        var value: Expr? = null
        if (!check(TokenType.RIGHT_BRACE)) {
            value = expression()
        }
        return Statement.Return(keyword, value)
    }

    private fun whileStatement(): Statement {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.")
        val body: Statement = statement()
        return Statement.While(condition, body)
    }

    private fun block(): List<Statement?> {
        val statements: MutableList<Statement?> = mutableListOf()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration())
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    private fun expressionStatement(): Statement {
        val expr = expression()
        return Statement.Expression(expr)
    }

    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr = or()
        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment()
            when (expr) {
                is Expr.Variable -> {
                    val name = expr.name
                    return Assign(name, value)
                }

                is Expr.Get -> {
                    return Expr.Set(expr.obj, expr.name, value)
                }

                is Expr.Accessor -> {
                    return Expr.AccessorSet(expr, value)
                }

                else -> LRuntime.error(ParserError(equals, "Invalid assignment target."))
            }
        }
        return expr
    }

    private fun or(): Expr {
        var expr: Expr = and()
        while (match(TokenType.OR)) {
            val operator = previous()
            val right: Expr = and()
            expr = Logical(expr, operator, right)
        }
        return expr
    }

    private fun and(): Expr {
        var expr = equality()
        while (match(TokenType.AND)) {
            val operator = previous()
            val right = equality()
            expr = Logical(expr, operator, right)
        }
        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr: Expr = term()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right: Expr = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr: Expr = factor()
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()

            expr = incDec(operator, expr)
        }
        return expr
    }

    private fun incDec(operator: Token, expr: Expr): Expr {
        return if (peek().type == TokenType.EQUAL) {
            incDecAssignment(operator, previous(2))
        } else {
            when (peek().type) {
                TokenType.PLUS -> {
                    val identifier = previous(2)
                    advance()
                    Expr.Increment(identifier)
                }

                TokenType.MINUS -> {
                    val identifier = previous(2)
                    advance()
                    Expr.Decrement(identifier)
                }

                else -> {
                    val right: Expr = factor()
                    Expr.Binary(expr, operator, right)
                }
            }
        }
    }

    private fun incDecAssignment(operator: Token, identifier: Token): Expr {
        advance()
        return when (operator.type) {
            TokenType.PLUS -> {
                Expr.AssignIncrement(identifier, expression())
            }

            TokenType.MINUS -> {
                Expr.AssignDecrement(identifier, expression())
            }

            else -> {
                throw ParserError(operator, "Unknown assignment operator ${operator.lexeme}")
            }
        }
    }

    private fun factor(): Expr {
        var expr: Expr = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right: Expr = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return call()
    }

    private fun accessor(callee: Expr): Expr {
        val rest = term()

        consume(TokenType.RIGHT_BRACKET, "Expect closing ].")
        val accessorToken = tokens[current - 2]

        return Expr.Accessor(callee, rest, accessorToken)
    }

    private fun call(): Expr {
        var expr = primary()
        while (true) {
            expr = if (match(TokenType.LEFT_PAREN)) {
                finishCall(expr)
            } else if (match(TokenType.DOT)) {
                val name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.")
                Expr.Get(expr, name)
            } else {
                break
            }
        }
        if (match(TokenType.LEFT_BRACKET)) {
            expr = accessor(expr)
        }
        return expr
    }

    private fun finishCall(callee: Expr): Expr {
        val arguments: MutableList<Expr> = ArrayList()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size >= 255) {
                    LRuntime.error(ParserError(peek(), "Can't have more than 255 arguments."))
                }
                arguments.add(expression())
            } while (match(TokenType.COMMA))
        }
        val paren = consume(
            TokenType.RIGHT_PAREN,
            "Expect ')' after arguments."
        )
        return Expr.Call(callee, paren, arguments)
    }

    private fun lambda(): Expr {
        consume(TokenType.LEFT_PAREN, "Expect '(' after function literal")
        val parameters = mutableListOf<Token>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) {
                    LRuntime.error(ParserError(peek(), "Cannot have more than 255 parameters"))
                }
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameters name."))
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
        consume(TokenType.LEFT_BRACE, "Expect '(' before function literal body")
        val body: List<Statement?> = block()
        return Expr.Lambda(previous().line, parameters, body)
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE)) return Expr.Literal(false)
        if (match(TokenType.TRUE)) return Expr.Literal(true)
        if (match(TokenType.NIL)) return Expr.Literal(null)
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.SUPER)) {
            val keyword = previous()
            consume(TokenType.DOT, "Expect '.' after 'super'.")
            val method = consume(TokenType.IDENTIFIER, "Expect superclass method name.")
            return Expr.Super(keyword, method)
        }
        if (match(TokenType.THIS)) return Expr.This(previous())
        if (match(TokenType.IDENTIFIER)) return Expr.Variable(previous())

        if (match(TokenType.FUN)) {
            return lambda()
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw ParserError(peek(), "Expect expression.")
    }

    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type === TokenType.SEMICOLON) return
            when (peek().type) {
                TokenType.CLASS, TokenType.FUN, TokenType.VAR, TokenType.FOR, TokenType.IF, TokenType.WHILE, TokenType.PRINT, TokenType.RETURN -> return
                else -> {}
            }
            advance()
        }
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        LRuntime.error(ParserError(peek(), message))
        throw ParserError(peek(), message)
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean {
        return if (isAtEnd()) false else peek().type === type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type === TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun previous(distance: Int): Token {
        return tokens[current - distance]
    }
}

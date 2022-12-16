package pl.bfelis.llang.language.interpreter.native.klass

import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.interpreter.Environment
import pl.bfelis.llang.language.interpreter.LFunction
import pl.bfelis.llang.language.scanner.Token
import pl.bfelis.llang.language.scanner.TokenType

fun getNativeMethodForLClass(name: String, params: List<String>, isInit: Boolean, env: Environment): LFunction {
    return LFunction(
        Statement.Function(
            Token(TokenType.FUN, name, name, -1),
            params.map { Token(TokenType.IDENTIFIER, name, name, -1) }.toList(),
            emptyList()
        ),
        env,
        isInit,
        !isInit
    )
}

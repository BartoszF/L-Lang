package pl.bfelis.fc93.language.interpreter.native.klass

import pl.bfelis.fc93.language.ast.Statement
import pl.bfelis.fc93.language.interpreter.Environment
import pl.bfelis.fc93.language.interpreter.LFunction
import pl.bfelis.fc93.language.scanner.Token
import pl.bfelis.fc93.language.scanner.TokenType

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

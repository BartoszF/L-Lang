package pl.bfelis.llang.language.interpreter.lnative.klass

import pl.bfelis.llang.language.ast.Statement
import pl.bfelis.llang.language.interpreter.Environment
import pl.bfelis.llang.language.interpreter.LFunction
import pl.bfelis.llang.language.scanner.Token
import pl.bfelis.llang.language.scanner.TokenType

data class NativeMethods(
    val methods: MutableMap<String, LFunction> = mutableMapOf(),
    val staticMethods: MutableMap<String, LFunction> = mutableMapOf()
)

fun getNativeMethodForLClass(name: String, params: List<String>, isInit: Boolean, env: Environment): LFunction {
    return LFunction(
        Statement.Function(
            Token(TokenType.FUN, name, name, -1),
            params.map { Token(TokenType.IDENTIFIER, name, name, -1) }.toList(),
            emptyList(),
            false
        ),
        env,
        isInit,
        !isInit,
        isStatic = false
    )
}

fun getNativeStaticMethodForLClass(name: String, params: List<String>, env: Environment): LFunction {
    return LFunction(
        Statement.Function(
            Token(TokenType.FUN, name, name, -1),
            params.map { Token(TokenType.IDENTIFIER, name, name, -1) }.toList(),
            emptyList(),
            true
        ),
        env,
        isInitializer = false,
        isNative = true,
        isStatic = true
    )
}

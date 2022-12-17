package pl.bfelis.llang.language.interpreter.native

import pl.bfelis.llang.language.interpreter.Environment
import pl.bfelis.llang.language.interpreter.native.function.Clock
import pl.bfelis.llang.language.interpreter.native.function.ToString
import pl.bfelis.llang.language.interpreter.native.klass.LArray
import pl.bfelis.llang.language.interpreter.native.klass.LList

object Globals {
    val values = { env: Environment ->
        mutableMapOf(
            "clock" to Clock,
            "string" to ToString,
            "Array" to LArray(env),
            "List" to LList(env)
        )
    }
}
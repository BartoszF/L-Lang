package pl.bfelis.fc93.language.interpreter.native

import pl.bfelis.fc93.language.interpreter.Environment
import pl.bfelis.fc93.language.interpreter.native.function.Clock
import pl.bfelis.fc93.language.interpreter.native.function.ToString
import pl.bfelis.fc93.language.interpreter.native.klass.LArray
import pl.bfelis.fc93.language.interpreter.native.klass.LList

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

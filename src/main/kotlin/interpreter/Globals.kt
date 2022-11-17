package pl.bfelis.fc93.language.interpreter

import pl.bfelis.fc93.language.interpreter.native.function.Clock
import pl.bfelis.fc93.language.interpreter.native.function.ToString
import pl.bfelis.fc93.language.interpreter.native.klass.LArray

object Globals {
    val values = { env: Environment ->
        mutableMapOf(
            "clock" to Clock,
            "string" to ToString,
            "Array" to LArray(env)
        )
    }
}

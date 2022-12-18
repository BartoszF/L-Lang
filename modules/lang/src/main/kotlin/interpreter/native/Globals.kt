package pl.bfelis.llang.language.interpreter.native

import pl.bfelis.llang.language.interpreter.Environment
import pl.bfelis.llang.language.interpreter.native.function.*
import pl.bfelis.llang.language.interpreter.native.klass.LArray
import pl.bfelis.llang.language.interpreter.native.klass.LFile
import pl.bfelis.llang.language.interpreter.native.klass.LList

object Globals {
    val values = { env: Environment ->
        mutableMapOf(
            "clock" to Clock,
            "readLine" to ReadLine,
            "printLine" to PrintLine,
            "print" to Print,
            "string" to ToString,
            "Array" to LArray(env),
            "List" to LList(env),
            "File" to LFile(env)
        )
    }
}

package pl.bfelis.llang.language.interpreter.lnative

import pl.bfelis.llang.language.interpreter.Environment
import pl.bfelis.llang.language.interpreter.lnative.function.*
import pl.bfelis.llang.language.interpreter.lnative.klass.LFile
import pl.bfelis.llang.language.interpreter.lnative.klass.collections.LArray
import pl.bfelis.llang.language.interpreter.lnative.klass.collections.LList
import pl.bfelis.llang.language.interpreter.lnative.klass.httpServer.LHttpServer

object Globals {
    val values = { env: Environment ->
        mutableMapOf(
            "clock" to Clock,
            "readLine" to ReadLine,
            "printLine" to PrintLine,
            "print" to Print,
            "string" to ToString,
            "size" to Size,
            "range" to Range,
            "Array" to LArray(env),
            "List" to LList(env),
            "File" to LFile(env),
            "HttpServer" to LHttpServer(env)
        )
    }
}

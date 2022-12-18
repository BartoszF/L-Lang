package pl.bfelis.llang.language.interpreter.lnative.klass.httpServer

import com.sun.net.httpserver.HttpExchange
import pl.bfelis.llang.language.interpreter.*
import pl.bfelis.llang.language.interpreter.lnative.klass.NativeMethods
import pl.bfelis.llang.language.interpreter.lnative.klass.getNativeMethodForLClass
import pl.bfelis.llang.language.interpreter.lnative.klass.stream.LOutputStream
import pl.bfelis.llang.language.interpreter.lnative.klass.stream.LOutputStreamInstance

val httpExchangeMethods = { env: Environment ->
    NativeMethods(
        mutableMapOf(
            "responseStream" to getNativeMethodForLClass("responseStream", emptyList(), false, env),
            "sendHeaders" to getNativeMethodForLClass("sendHeaders", listOf("status", "length"), false, env)
        )
    )
}

class LHttpExchange(env: Environment) :
    LNativeClass("HttpExecutor", null, env, httpExchangeMethods(env).methods, httpExchangeMethods(env).staticMethods)

class LHttpExchangeInstance(klass: LHttpExchange, val exchange: HttpExchange) : LNativeInstance(klass) {
    val env = klass.env

    override fun nativeFn(name: String): Any {
        return when (name) {
            "sendHeaders" -> object : LCallable {
                override fun arity(): Int = 2

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    val status = (arguments[0] as Double).toInt()
                    val length = (arguments[1] as Number).toLong()

                    exchange.sendResponseHeaders(status, length)

                    return null
                }
            }

            "responseStream" -> object : LCallable {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                    return LOutputStreamInstance(LOutputStream(env), exchange.responseBody)
                }
            }

            else -> {
                throw RuntimeError(null, "Unknown method $name")
            }
        }
    }
}

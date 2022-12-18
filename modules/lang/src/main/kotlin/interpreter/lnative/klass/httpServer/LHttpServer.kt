package pl.bfelis.llang.language.interpreter.lnative.klass.httpServer

import com.sun.net.httpserver.HttpServer
import pl.bfelis.llang.language.interpreter.*
import pl.bfelis.llang.language.interpreter.lnative.klass.NativeMethods
import pl.bfelis.llang.language.interpreter.lnative.klass.getNativeMethodForLClass
import java.net.InetSocketAddress

val httpServerMethods = { env: Environment ->
    NativeMethods(
        mutableMapOf(
            "init" to
                getNativeMethodForLClass("init", listOf("port"), true, env),
            "path" to getNativeMethodForLClass("path", listOf("path", "handler"), false, env),
            "getAddress" to getNativeMethodForLClass("getAddress", emptyList(), false, env),
            "start" to getNativeMethodForLClass("start", emptyList(), false, env)
        )
    )
}

class LHttpServer(env: Environment) :
    LNativeClass("HttpServer", null, env, httpServerMethods(env).methods, httpServerMethods(env).staticMethods) {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val port = (arguments[0] as Double).toInt()
        return LHttpServerInstance(this, port)
    }
}

class LHttpServerInstance(klass: LHttpServer, port: Int) : LNativeInstance(klass) {
    private val server: HttpServer = HttpServer.create(InetSocketAddress(port), 1)
    private val env = klass.env

    override fun nativeFn(name: String): Any {
        return when (name) {
            "getAddress" -> object : LCallable {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                    return server.address.toString()
                }
            }

            "path" -> object : LCallable {
                override fun arity(): Int = 2

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    val path = arguments[0] as String
                    val handler = arguments[1] as LFunction

                    server.createContext(path) { exchange ->
                        val lExchange = LHttpExchangeInstance(LHttpExchange(env), exchange)
                        handler.call(interpreter, listOf(lExchange))
                    }

                    return null
                }
            }

            "start" -> object : LCallable {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    server.executor = null
                    server.start()
                    return null
                }
            }

            else -> {
                throw RuntimeError(null, "Unknown method $name")
            }
        }
    }
}

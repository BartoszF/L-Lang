package pl.bfelis.llang.language.interpreter.lnative.klass.stream

import pl.bfelis.llang.language.interpreter.*
import pl.bfelis.llang.language.interpreter.lnative.klass.NativeMethods
import pl.bfelis.llang.language.interpreter.lnative.klass.getNativeMethodForLClass
import java.io.OutputStream

val outputStreamMethods = { env: Environment ->
    NativeMethods(
        mutableMapOf(
            "write" to getNativeMethodForLClass("write", listOf("content"), false, env),
            "close" to getNativeMethodForLClass("close", emptyList(), false, env)
        )
    )
}

class LOutputStream(env: Environment) :
    LNativeClass("OutputStream", null, env, outputStreamMethods(env).methods, outputStreamMethods(env).staticMethods)

class LOutputStreamInstance(klass: LOutputStream, val os: OutputStream) : LNativeInstance(klass) {
    override fun nativeFn(name: String): Any {
        return when (name) {
            "write" -> object : LCallable {
                override fun arity(): Int = 1

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    val contents = arguments[0] as String

                    os.write(contents.toByteArray())

                    return null
                }
            }

            "close" -> object : LCallable {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    os.close()

                    return null
                }
            }

            else -> throw RuntimeError(null, "Unknown method $name")
        }
    }
}

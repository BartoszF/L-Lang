package pl.bfelis.llang.language.interpreter.native.klass

import pl.bfelis.llang.language.interpreter.*
import java.io.File
import java.nio.file.Paths

val fileMethods = { env: Environment ->
    NativeMethods(
        mutableMapOf(
            "init" to
                getNativeMethodForLClass("init", listOf("path"), true, env),
            "exists" to
                getNativeMethodForLClass("exists", emptyList(), false, env),
            "isDir" to
                getNativeMethodForLClass("isDir", emptyList(), false, env),
            "isFile" to
                getNativeMethodForLClass("isFile", emptyList(), false, env)
        ),
        mutableMapOf(
            "pathExists" to getNativeStaticMethodForLClass("pathExists", listOf("path"), env),
            "currentDir" to getNativeStaticMethodForLClass("currentDir", emptyList(), env)

        )
    )
}

class LFile(env: Environment) : LNativeClass("File", null, fileMethods(env).methods, fileMethods(env).staticMethods) {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val path = arguments[0] as String
        return LFileInstance(this, path)
    }

    override fun nativeStaticFn(name: String): LFunction? {
        val function = staticMethods[name]!!
        return when (name) {
            "pathExists" -> object : LFunction(function) {
                override fun arity(): Int = 1

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    val path = arguments[0] as String
                    return File(path).exists()
                }
            }

            "currentDir" -> object : LFunction(function) {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    return Paths.get("").toAbsolutePath().toString()
                }
            }

            else -> {
                throw RuntimeError(null, "Unknown method $name")
            }
        }
    }
}

class LFileInstance(klass: LFile, path: String) : LNativeInstance(klass) {
    private val file = File(path)

    override fun nativeFn(name: String): Any? {
        return when (name) {
            "exists" -> object : LCallable {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    return file.exists()
                }
            }
            "isFile" -> object : LCallable {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    return file.isFile
                }
            }
            "isDir" -> object : LCallable {
                override fun arity(): Int = 0

                override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
                    return file.isDirectory
                }
            }
            else -> {
                throw RuntimeError(null, "Unknown method $name")
            }
        }
    }
}

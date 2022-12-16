package pl.bfelis.llang.language

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional
import pl.bfelis.llang.language.error.ResolverError
import pl.bfelis.llang.language.interpreter.RuntimeError
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = ArgParser("llang")

    val input = parser.argument(ArgType.String, "script", description = "Script to run. If not specified will run in interactive mode.").optional()
    val debug = parser.option(ArgType.Boolean, "debug", "d", "Debug mode. Will print some arcane staff.")

    parser.parse(args)

    if (!input.value.isNullOrEmpty()) {
        runFile(input.value!!, debug.value ?: false)
    } else {
        runPrompt()
    }
}

@Throws(IOException::class)
private fun runFile(path: String, debug: Boolean = false) {
    run(File(path), debug)
}

@Throws(IOException::class)
private fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)
    val runtime = LRuntime()
    while (true) {
        print("> ")
        val line = reader.readLine() ?: break
        try {
            runtime.run(line)
        } catch (e: ResolverError) {
            resolverError(e)
        } catch (e: RuntimeError) {
            runtimeError(e)
        }
    }
}

private fun run(file: File, debug: Boolean = false) {
    try {
        LRuntime(debug).run(file)
    } catch (e: ResolverError) {
        resolverError(e)
        exitProcess(70)
    } catch (e: RuntimeError) {
        runtimeError(e)
        exitProcess(70)
    }
}

fun resolverError(error: ResolverError) {
    System.err.println(
        """
            ${error.message} {${error.token?.lexeme}}
        """.trimIndent()
    )
}

fun runtimeError(error: RuntimeError) {
    System.err.println(
        """
            ${error.message}
        """.trimIndent()
    )
}

package pl.bfelis.fc93.language

import pl.bfelis.fc93.language.interpreter.RuntimeError
import pl.bfelis.fc93.language.resolver.ResolverError
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset.defaultCharset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: language [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

@Throws(IOException::class)
private fun runFile(path: String) {
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, defaultCharset()))
}

@Throws(IOException::class)
private fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)
    while (true) {
        print("> ")
        val line = reader.readLine() ?: break
        try {
            run(line)
        } catch (e: ResolverError) {
            resolverError(e)
        } catch (e: RuntimeError) {
            runtimeError(e)
        }
    }
}

private fun run(source: String) {
    try {
        LRuntime().run(source)
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
            ${error.message} {${error.name.lexeme}}
            [line ${error.name.line}]
        """.trimIndent()
    )
}

fun runtimeError(error: RuntimeError) {
    System.err.println(
        """
            ${error.message}
            [line ${error.token!!.line}]
        """.trimIndent()
    )
}

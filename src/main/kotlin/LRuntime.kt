package pl.bfelis.llang.language

import pl.bfelis.llang.language.ast.AstPrinter
import pl.bfelis.llang.language.error.*
import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.parser.Parser
import pl.bfelis.llang.language.resolver.Resolver
import pl.bfelis.llang.language.scanner.Scanner
import java.io.File
import java.nio.charset.Charset

class LRuntime(private val debug: Boolean = false) {
    val interpreter = Interpreter()
    private val resolver = Resolver(interpreter, this)
    private val astPrinter = AstPrinter()

    init {
        errorNotifier.register(errorPrinter)
    }

    fun run(file: File, nested: Boolean = false) {
        run(String(file.readBytes(), Charset.defaultCharset()), file.path, nested)
    }

    fun run(source: String, fileName: String? = null, nested: Boolean = false) {
        val scanner = Scanner(source, fileName)
        val tokens = scanner.scanTokens()

        if (debug) {
            println("FILE: $fileName")
            println("## Scanned tokens ##")
            tokens.forEach { println(it) }
            println()
        }

        if (hadError) {
            errorNotifier.notifyErrors()
            return
        }

        val parser = Parser(tokens, fileName)
        val statements = parser.parse()

        if (hadError) {
            errorNotifier.notifyErrors()
            return
        }

        if (debug) {
            println("## AST ##")
            val ast = astPrinter.printStatements(statements)
            println(ast)
            println()
        }

        resolver.resolve(statements, fileName = fileName)

        if (!nested) {
            errorNotifier.notifyErrors() // Notify no matter what - there could be warnings
        }

        if (hadError) return

        interpreter.interpret(statements, fileName)
    }

    fun getEnvironment() = interpreter.globals

    fun registerErrorObserver(observer: ErrorObserver) {
        errorNotifier.register(observer)
    }

    companion object {
        var hadError = false
        private val errorNotifier = ErrorNotifier()
        private val errorPrinter = ErrorPrinter()

        fun warn(warning: Warning) {
            errorNotifier.error(warning)
        }

        fun error(error: GeneralLException) {
            errorNotifier.error(error)
            hadError = true
        }
    }
}

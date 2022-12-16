package pl.bfelis.llang.language

import pl.bfelis.llang.language.ast.AstPrinter
import pl.bfelis.llang.language.error.*
import pl.bfelis.llang.language.interpreter.Interpreter
import pl.bfelis.llang.language.parser.Parser
import pl.bfelis.llang.language.resolver.Resolver
import pl.bfelis.llang.language.scanner.Scanner

class LRuntime(private val debug: Boolean = false) {
    val interpreter = Interpreter()
    val resolver = Resolver(interpreter)
    private val astPrinter = AstPrinter()

    fun run(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        if (debug) {
            tokens.forEach { println(it) }
        }

        errorNotifier.register(errorPrinter)

        if (hadError) {
            errorNotifier.notifyErrors()
            return
        }

        val parser = Parser(tokens)
        val statements = parser.parse()

        if (hadError) {
            errorNotifier.notifyErrors()
            return
        }

        if (debug) {
            val ast = astPrinter.printStatements(statements)
            println(ast)
        }

        resolver.resolve(statements)

        errorNotifier.notifyErrors() // Notify no matter what - there could be warnings

        if (hadError) return

        interpreter.interpret(statements)
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

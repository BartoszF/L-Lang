package pl.bfelis.fc93.language

import pl.bfelis.fc93.language.ast.AstPrinter
import pl.bfelis.fc93.language.error.*
import pl.bfelis.fc93.language.interpreter.Interpreter
import pl.bfelis.fc93.language.parser.Parser
import pl.bfelis.fc93.language.resolver.Resolver
import pl.bfelis.fc93.language.scanner.Scanner

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

        Resolver(interpreter).resolve(statements)

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

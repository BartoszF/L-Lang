package pl.bfelis.llang.language.util

import java.io.IOException
import java.io.PrintWriter
import java.util.*
import kotlin.system.exitProcess

class ASTGenerator {
    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                System.err.println("Usage: generate_ast <output directory>")
                exitProcess(64)
            }
            val outputDir = args[0]
            listOf("Expr", "Statement").forEach {
                val lines = object {}.javaClass.getResourceAsStream("/ast/$it")?.bufferedReader()?.readLines()
                defineAst(outputDir, it, lines!!)
            }
        }

        @Throws(IOException::class)
        private fun defineAst(
            outputDir: String,
            baseName: String,
            types: List<String>
        ) {
            val path = "$outputDir/$baseName.kt"
            val writer = PrintWriter(path, "UTF-8")
            writer.println("package pl.bfelis.llang.language.ast")
            writer.println()
            writer.println("import pl.bfelis.llang.language.scanner.Token")
            writer.println()
            writer.println("abstract class $baseName {")

            defineVisitor(writer, baseName, types)

            for (type in types) {
                val className = type.split(":".toRegex()).toTypedArray()[0].trim { it <= ' ' }
                val fields = type.split(":".toRegex()).toTypedArray()[1].trim { it <= ' ' }
                defineType(writer, baseName, className, fields)
            }

            writer.println()
            writer.println("  abstract fun <R> accept(visitor: Visitor<R>, fileName: String? = null) : R")

            writer.println("}")
            writer.close()
        }

        private fun defineType(
            writer: PrintWriter,
            baseName: String,
            className: String,
            fieldList: String
        ) {
            writer.println("  class $className(")

            val fields = fieldList.split(", ".toRegex()).toTypedArray()

            for (field: String in fields) {
                val name = field.split(" ".toRegex()).toTypedArray()[1]
                val type = field.split(" ".toRegex()).toTypedArray()[0]
                writer.println("    val $name : $type${if (fields.indexOf(field) != fields.size) "," else ""}")
            }

            writer.println(") : $baseName() {")

            writer.println()
            writer.println("    override fun <R> accept(visitor: Visitor<R>, fileName: String?): R {")
            writer.println("        return visitor.visit$className$baseName(this, fileName)")
            writer.println("    }")
            writer.println("  }")
        }

        private fun defineVisitor(
            writer: PrintWriter,
            baseName: String,
            types: List<String>
        ) {
            writer.println("  interface Visitor<R> {")
            for (type: String in types) {
                val typeName = type.split(":".toRegex()).toTypedArray()[0].trim { it <= ' ' }
                writer.println(
                    "    fun visit$typeName$baseName(${baseName.lowercase(Locale.getDefault())} : $typeName, fileName: String? = null): R"
                )
            }
            writer.println("  }")
        }
    }
}

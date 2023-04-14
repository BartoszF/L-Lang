package pl.bfelis.llang.language.interpreter.lnative.klass.iterators

import pl.bfelis.llang.language.interpreter.*
import pl.bfelis.llang.language.interpreter.lnative.LIterable
import pl.bfelis.llang.language.interpreter.lnative.klass.getNativeMethodForLClass
import java.util.stream.IntStream.range
import kotlin.streams.toList

val RangeMethods = { env: Environment ->
    mutableMapOf(
        "init" to
            getNativeMethodForLClass("init", emptyList(), true, env),
        "size" to getNativeMethodForLClass("size", emptyList(), false, env),
        "map" to getNativeMethodForLClass("map", listOf("function"), false, env),
        "each" to getNativeMethodForLClass("each", listOf("function"), false, env),
        "iterator" to getNativeMethodForLClass("iterator", emptyList(), false, env)
    )
}

class LRange(env: Environment) : LNativeClass("Range", null, env, RangeMethods(env)) {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        return RangeInstance(this)
    }
}

class RangeInstance(klass: LRange, val start: Double = 0.0, val end: Double = 10.0, var current: Double = start) :
    LNativeInstance(klass),
    LIterable {

    override fun nativeFn(name: String): Any {
        throw RuntimeError(null, "Unknown method $name")
    }

    override fun next(): Any {
        return current++
    }

    override fun getIndex(): Double {
        return current
    }

    override fun size(): Double {
        return end - start
    }

    override fun atEnd(): Boolean {
        return current == end
    }

    override fun slice(start: Int, count: Int?): MutableList<Any?> {
        return range(
            this.start.toInt() + start,
            (count?.let { this.start.toInt() + start + count } ?: this.end.toInt()).coerceAtMost(this.end.toInt())
        )
            .toList()
            .map { it.toDouble() } // L operates on Doubles
            .toMutableList()
    }

    override fun toString(): String {
        return "Range@[${range(start.toInt(), end.toInt()).toList().map { it.toDouble() }.joinToString(", ")}]"
    }
}

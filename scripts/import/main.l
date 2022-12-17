import("functions.l")
import("nested/add.l")

fun function() {
    return "FUNCTION SUCCESS"
}

testFunction(function)
testFunction(fun () { return "LAMBDA SUCCESS" })
printLine(add(1,2))

fun curry(f) {
    fun f1(a) {
      fun f2(b) {
        return f(a, b)
      }

      return f2
    }

    return f1
}

fun curryLambda(f) {
    return fun (a) {
      return fun (b) {
        return f(a, b)
      }
    }
}

fun sum(a, b) {
    return a + b
}

var curried = curryLambda(sum)

printLine(curried(2)(3))

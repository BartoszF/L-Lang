import("functions.l")

fun function() {
    return "FUNCTION SUCCESS"
}

testFunction(function)
testFunction(fun () { return "LAMBDA SUCCESS" })

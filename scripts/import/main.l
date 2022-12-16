import("functions.l")
import("nested/add.l")

fun function() {
    return "FUNCTION SUCCESS"
}

testFunction(function)
testFunction(fun () { return "LAMBDA SUCCESS" })
print(add(1,2))

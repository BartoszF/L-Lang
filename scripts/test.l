var xyz = 1

var a = 2
printLine 1
printLine a

printLine "Print (2 + (4 / 2) - 3 * (2 - ( 2 * 18)))"
var b = (2 + (4 / 2) - 3 * (2 - (2 * 18)))
printLine b

var d = "a"
printLine d

{
 var a = "test"
 printLine a
}

printLine a
a = "4"
printLine a

printLine ""
// Test
{
    printLine "I will be 5. Then decreasing by 1"

    var i = 5
    while(i>0) {
        printLine i
        i=i-1
    }
}

printLine clock()

{
    printLine "Give me first 21 elements of fib"
    var a = 0
    var temp

    var f = 1

    while(a < 10000) {
          printLine a
          temp = a
          a = f
          f = temp + f
    }
}

printLine clock()

printLine clock

fun test() {
    printLine "INSIDE FUNCTION"
}

printLine test
test()

fun willReturn(a) {
    return a + 1
}

printLine "FUNCTION RETURN"
printLine willReturn(2)

fun makeCounter() {
    var i = 0
    fun count() {
        i = i + 1
        printLine i
    }

    return count
}

printLine "CLOSURE TEST"
var counter = makeCounter()
counter()
counter()
counter()

class Test {
    init() {
        this.a = "TEST"
        printLine "DUPA"
    }

    test() {
        printLine a
    }
}

var t = Test()
t.test()

fun empty() {
    printLine "TEST"
    return
}

empty()

class Vector {
    init(x, y) {
        this.x = x
        this.y = y
    }
}

printLine(Vector(2,3).x)

var x = 0
var y = 0

fun testVector() {
    printLine(x)
    printLine(y)
    var vec = Vector(x,y)
    printLine(vec)
}

testVector()

class TEST {
    init(a,b) {
        this.a = a
        this.b = b
    }

    test() {
        var x = this.b[1]
        printLine(x)
        if(this.a >= 12) {
            printLine(this.b)
        } else {
            for(x in range(0,3)) {
                printLine("NOPE")
            }
        }
    }
}

TEST(2, "abc").test()

fun getString() {
    return "TEST"
}

fun getNumber() {
    return 1
}

printLine(getString()[1])

var z = 1
printLine(getString()[z])
printLine(getString()[getNumber()])

var t = "TEST"
t[1] = "3"

printLine(t)

var arr = Array(2)

printLine(arr[0])
arr[0] = 2
printLine(arr[0])
printLine("Size is " + string(arr.size()))

var list = List()
list.add(12)

printLine("Size is " + string(list.size()))
printLine(list[0])

fun testLambda(t) {
    var res = t()
    printLine("LAMBDA RESULT: " + string(res))
}

testLambda(fun () { return 2 })

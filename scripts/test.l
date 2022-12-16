var xyz = 1

var a = 2
print 1
print a

print "Print (2 + (4 / 2) - 3 * (2 - ( 2 * 18)))"
var b = (2 + (4 / 2) - 3 * (2 - (2 * 18)))
print b

var d = "a"
print d

{
 var a = "test"
 print a
}

print a
a = "4"
print a

print ""
// Test
{
    print "I will be 5. Then decreasing by 1"

    var i = 5
    while(i>0) {
        print i
        i=i-1
    }
}

print clock()

{
    print "Give me first 21 elements of fib"
    var a = 0
    var temp

    for (var f = 1; a < 10000; f = temp + f) {
      print a
      temp = a
      a = f
    }
}

print clock()

print clock

fun test() {
    print "INSIDE FUNCTION"
}

print test
test()

fun willReturn(a) {
    return a + 1
}

print "FUNCTION RETURN"
print willReturn(2)

fun makeCounter() {
    var i = 0
    fun count() {
        i = i + 1
        print i
    }

    return count
}

print "CLOSURE TEST"
var counter = makeCounter()
counter()
counter()
counter()

class Test {
    init() {
        this.a = "TEST"
        print "DUPA"
    }

    test() {
        print a
    }
}

var t = Test()
t.test()

fun empty() {
    print "TEST"
    return
}

empty()

class Vector {
    init(x, y) {
        this.x = x
        this.y = y
    }
}

print(Vector(2,3).x)

var x = 0
var y = 0

fun testVector() {
    print(x)
    print(y)
    var vec = Vector(x,y)
    print(vec)
}

testVector()

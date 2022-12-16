# L Language

Interpreted, dynamically-typed language written in Kotlin.

Based on jlox from [Crafting Interpreters](https://craftinginterpreters.com), but further modified (some syntax changes and more features).

Inspired by JavaScript and Kotlin.

### Syntax overview

```
class TEST {
    init(a,b) {
        this.a = a
        this.b = b
    }

    test() {
        var x = this.b[1]
        print(x)
        if(this.a >= 12) {
            print(this.b)
        } else {
            for(var x = 0; x < 3; x++) {
                print("NOPE")
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

print(getString()[1])

var z = 1
print(getString()[z])
print(getString()[getNumber()])

var t = "TEST"
t[1] = "3"

print(t)

var arr = Array(2)

print(arr[0])
arr[0] = 2
print(arr[0])
print("Size is " + string(arr.size()))

var list = List()
list.add(12)

print("Size is " + string(list.size()))
print(list[0])

fun testLambda(t) {
    var res = t()
    print("LAMBDA RESULT: " + string(res))
}

testLambda(fun () { return 2 })

val constant = 12
print(constant)

constant = 13 // This will throw an error

```

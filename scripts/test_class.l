class Test {
  init(a, b) {
    this.a = a
    this.b = b
  }

  add() {
    return this.a + this.b
  }
}

val test = Test(1,2)
printLine(test.add())
printLine(test.a)
printLine(test.b)

test.a = 5
printLine(test.add())

class SuperTest : Test {
    add() {
        printLine("HA HA, I will now add two numbers!")
        return super.add()
    }
}

val superTest = SuperTest(4,2)
printLine(superTest.add())

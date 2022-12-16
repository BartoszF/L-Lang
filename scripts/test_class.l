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
print(test.add())
print(test.a)
print(test.b)

test.a = 5
print(test.add())

class SuperTest : Test {
    add() {
        print("HA HA, I will now add two numbers!")
        return super.add()
    }
}

val superTest = SuperTest(4,2)
print(superTest.add())

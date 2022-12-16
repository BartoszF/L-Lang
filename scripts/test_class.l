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

}

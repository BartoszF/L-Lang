class Test {
    static printStatic() {
        printLine("STATIC")
    }

    notStatic() {
      return "NOT STATIC"
    }
}

printLine(Test.printStatic)
Test.printStatic()

printLine(Test().notStatic())

static fun test() {
  printLine("NOPE")
}

test()

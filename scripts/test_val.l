val test = "test"
var unused = 1

printLine(test)

//test = "test2" // Will throw error because of val reassign

printLine(test)

{
    printLine(test)
    var a = 1
    printLine(a)
}

for(var i = 0; i < 5; i = i + 1){
    val counter = i + 1
    printLine(counter)
}

//val asd // Will throw because of val without init

//printLine(asd)

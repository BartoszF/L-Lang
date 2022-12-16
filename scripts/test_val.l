val test = "test"
var unused = 1

print(test)

//test = "test2" // Will throw error because of val reassign

print(test)

{
    print(test)
    var a = 1
    print(a)
}

for(var i = 0; i < 5; i = i + 1){
    val counter = i + 1
    print(counter)
}

//val asd // Will throw because of val without init

//print(asd)

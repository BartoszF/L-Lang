val list = range(0,5)

printLine(list)
printLine(list[0:4])
printLine(list[1:3])
printLine(list[:3])
printLine(list[1:])

printLine("")
printLine([0, 1, 2, 3, 4][1:3])

fun getRange(a,b) {
    return range(a,b)
}

printLine(getRange(0,5))

for(i in getRange(0,5)) {
    printLine(i)
}

printLine(getRange(0,5)[1:3])

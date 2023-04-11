val list = [1,2,3,4,5,6]

printLine(list)
for(i in list) printLine(i)

printLine("")

val [a,b,c,rest] = list

printLine("A: " + string(a))
printLine("B: " + string(b))
printLine("C: " + string(c))
printLine("REST: " + string(rest))

val [x,y,z] = [1]

printLine("")
printLine("X: " + string(x))
printLine("Y: " + string(y))
printLine("Z: " + string(z))

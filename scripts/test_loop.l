val n = 0

while(n < 5) {
    printLine("WHILE " + string(n))
    if(n == 3) { break }
    n++
}

for(i in range(0,5)) {
    printLine("FOR " + string(i))
    if(i == 3) { break }
}

for(i in range(0,5)) {
    if(i == 3) { continue }
    printLine("FOR " + string(i))
}

fun test() {
    break
}

while(true) {
    test()
}

{
    break
    continue
}

val n = 0

while(n < 5) {
    printLine("WHILE " + string(n))
    if(n == 3) { break }
    n++
}

for(var i = 0; i < 5; i++) {
    printLine("FOR " + string(i))
    if(i == 3) { break }
}

for(var i = 0; i < 5; i++) {
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

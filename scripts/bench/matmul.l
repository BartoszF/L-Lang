printLine("Hello") // TODO: Fix static fun checking when fun is first thing

fun abs(a) {
    if(a < 0) return -a
    return a
}

fun matgen(n, seed) {
    val a = Array(n)
    for(i in range(0,n))
        a[i] = Array(n)
    val tmp = seed / n / n
    for (i in range(0,n)) {
        for (j in range(0,n)) {
            a[i][j] = tmp * (i - j) * (i + j)
        }
    }

    return a
}

fun matmul(a, b) {
    val m = a.size()
    val n = a[0].size()
    val p = b[0].size()

    val x=Array(m)
    for(i in range(0,m)) { x[i] = Array(p) }

    val c = Array(p)
    for(i in range(0,p)) { c[i] = Array(n) }

    for (i in range(0,n)) // transpose
        for (j in range(0,p))
            c[j][i] = b[i][j]
    for (i in range(0,m))
        for (j in range(0,p)) {
            var s = 0.0
            for (k in range(0,n))
                s += a[i][k] * c[j][k]
            x[i][j] = s
        }
    return x
}

fun calc(n) {
    val size = n / 2 * 2
    val a = matgen(size, 1.0)
    val b = matgen(size, 2.0)
    val x = matmul(a, b)
    return x[size / 2][size / 2]
}


val n = 100

val left = calc(n)
val right = -18.67
if (abs(left - right) > 0.1) {
    printLine(string(left) + " != " + string(right))
    exit(1)
}

val startTime = clock()
val results = calc(n)
val timeDiff = clock() - startTime

printLine(results)
printLine("time: " + string(timeDiff / 1000))

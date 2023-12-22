import kotlin.math.abs
import kotlin.system.exitProcess

fun matgen(n: Int, seed: Double): Array<DoubleArray> {
    val a = Array(n) { DoubleArray(n) }
    val tmp = seed / n / n
    for (i in 0 until n) {
        for (j in 0 until n) {
            a[i][j] = tmp * (i - j) * (i + j)
        }
    }

    return a
}

fun matmul(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
    val m = a.size
    val n = a[0].size
    val p = b[0].size
    val x = Array(m) { DoubleArray(p) }
    val c = Array(p) { DoubleArray(n) }
    for (i in 0 until n) // transpose
        for (j in 0 until p) c[j][i] = b[i][j]
    for (i in 0 until m) for (j in 0 until p) {
        var s = 0.0
        for (k in 0 until n) s += a[i][k] * c[j][k]
        x[i][j] = s
    }
    return x
}

fun calc(n: Int): Double {
    val size = n / 2 * 2
    val a = matgen(size, 1.0)
    val b = matgen(size, 2.0)
    val x = matmul(a, b)
    return x[size / 2][size / 2]
}

fun main() {
    val n = 100

    val left = calc(101)
    val right = -18.67
    if (abs(left - right) > 0.1) {
        System.err.printf("%f != %f\n", left, right)
        exitProcess(1)
    }

    val startTime = System.currentTimeMillis()
    val results = calc(n)
    val timeDiff = System.currentTimeMillis() - startTime

    println(results)
    println("time: ${timeDiff / 1e3}")
}

main()

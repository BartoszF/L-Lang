
val server = HttpServer(8000)

val testFile = File("test.html")
val contents = testFile.lines()
var length = 0

for(i in range(0,contents.size())) {
    length += size(contents[i])
}

server.path("/test", fun (t) {
    printLine(string(clock()) + " - GET /test")

    t.sendHeaders(200, length)
    val os = t.responseStream()

    for(i in range(0,contents.size())) {
        os.write(contents[i])
    }
    os.close()
})

server.start()

printLine(server.getAddress())


val server = HttpServer(8000)

val testFile = File("test.html")
val contents = testFile.lines()
var length = 0

for(var i = 0; i < contents.size(); i++) {
    length += size(contents[i])
}

server.path("/test", fun (t) {
    printLine(string(clock()) + " - GET /test")

    t.sendHeaders(200, length)
    val os = t.responseStream()

    for(var i = 0; i < contents.size(); i++) {
        os.write(contents[i])
    }
    os.close()
})

server.start()

printLine(server.getAddress())

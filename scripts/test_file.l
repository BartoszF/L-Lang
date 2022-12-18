
printLine(File.currentDir())

printLine("test_file.l exists: " + string(File.pathExists("test_file.l")))
printLine("test_notExisting.l exists: " + string(File.pathExists("test_notExisting.l")))

val file = File("test_file.l")

printLine("test_file.l exists: " + string(file.exists()))
printLine("test_file.l isFile: " + string(file.isFile()))
printLine("test_file.l isDir: " + string(file.isDir()))

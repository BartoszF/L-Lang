
printLine(File.currentDir())

printLine("test_file.l exists: " + string(File.pathExists("test_file.l")))
printLine("test_notExisting.l exists: " + string(File.pathExists("test_notExisting.l")))

val file = File("test_file.l")

printLine("test_file.l exists: " + string(file.exists()))
printLine("test_file.l isFile: " + string(file.isFile()))
printLine("test_file.l isDir: " + string(file.isDir()))


val secondFile = File("test.txt")

secondFile.writeLine("Test")
secondFile.writeLine("New line")
secondFile.write("1 ")
secondFile.write("2")

printLine(secondFile.exists())

val lines = secondFile.lines()

printLine(lines)
printLine("Size: " + string(lines.size()))

printLine("File lines:")
for(var i = 0; i < lines.size(); i++) {
    printLine("  " + lines[i])
}

secondFile.delete()

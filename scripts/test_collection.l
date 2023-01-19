val l = List()

l.add("test")
l.add("test2")
l.add("test3")

l.each(fun (it) { printLine(it) })

l.map(fun (it) { return string(it) + "HEHE" }).each(fun (it) { printLine(it) })


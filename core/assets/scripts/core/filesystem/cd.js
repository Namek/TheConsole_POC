assert(args.length == 1, "gimme one argument!")

var path = args[0]
var file = new java.io.File(path)

assert(file.isDirectory(), String("Given path is not a directory: " + path.toString()))

Storage = Storage.getGlobalStorage("filesystem")
Storage.set("path", path)
Storage.save()
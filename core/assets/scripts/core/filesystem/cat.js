assert(args.length == 1, "expected argument: filename")

Storage = Storage.getGlobalStorage("filesystem")
path = Storage.get("path")

assert(!!path, "Current path is not set, please use `cd [path]`")

var filename = args[0]
path = String(path)
var filePath = java.nio.file.Paths.get(path).resolve(filename)
var file = filePath.toFile()

assert(file.isFile(), String("The path is not a file: " + path))

console.log(new java.lang.String(java.nio.file.Files.readAllBytes(filePath)))
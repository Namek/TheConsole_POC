assert(args.length < 2, "maximum one argument, please")

var path;

if (args.length == 1) {
	path = args[0]
}
else {
	Storage = Storage.getGlobalStorage("filesystem")
	path = Storage.get("path")
}

assert(!!path, "Path is invalid.")

var file = new java.io.File(path)
console.log(Java.from(file.list()).join('\n'))
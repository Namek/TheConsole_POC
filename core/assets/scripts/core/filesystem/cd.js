assert(args.length == 1, "gimme one argument!")

var path = args[0]

Storage = Storage.getGlobalStorage("filesystem")
Storage.set("path", path)
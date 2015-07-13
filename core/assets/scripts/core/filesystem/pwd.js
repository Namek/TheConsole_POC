assert(args.length == 0, "Please, no args.");

Storage = Storage.getGlobalStorage("filesystem")
console.log(Storage.get("path"))
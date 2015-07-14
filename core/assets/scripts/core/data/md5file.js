assert(args.length == 1, "Usage: md5file <filepath>")
var filepath = args[0];
var file = new java.io.File(filepath)

assert(file.isFile(), String("It is not a file: " + filepath))

var ByteArrayType = Java.type("byte[]")
var buffer = new ByteArrayType(8192)
var md = java.security.MessageDigest.getInstance("MD5")

var fileStream = new java.io.FileInputStream(file)
var dis = new java.security.DigestInputStream(fileStream, md)

try {
	while (dis.read(buffer) != -1) { }
}
catch (exc) {
	return exc
}
dis.close()

return javax.xml.bind.DatatypeConverter.printHexBinary(md.digest())
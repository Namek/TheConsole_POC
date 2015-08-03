assert(args.length <= 2, "maximum argument count: 2")

var rand = new java.util.Random()

if (args.length === 0) {
	return rand.nextInt()
}
else if (args.length === 1) {
	return rand.nextInt(args[0])
}
else if (args.length === 2) {
	var min = 1*args[0], max = 1*args[1]
	assert(min < max, "left value is not smaller than right value!")

	return min + rand.nextInt(max - min + 1)
}
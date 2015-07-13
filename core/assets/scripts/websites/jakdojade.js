assertInfo(args.length == 0,
	"Usage:\n" +
	"- jakdojade dokad\n" +
	"- jakdojade skad dokad"
)

var cityId = 2000 //Wrocław
var from = 'Wyspa Piasek'
var to

if (args.length == 1) {
	to = args[0]
}
else if (args.length == 2) {
	from = args[0]
	to = args[1]
}
else {
	from = args[0]
	to = args.splice(1).join(' ')
}

var url = "http://jakdojade.pl/?fn=" + from + "&tn=" + to + "&cid=" + cityId
Utils.openUrl(encodeURI(url))
console.hide()

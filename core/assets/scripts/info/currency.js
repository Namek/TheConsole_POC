assertInfo(args.length == 0,
	"Usages:\n" +
	" - currency 12 gbp eur\n" +
	" - currency gbp eur"
)

var amount = args.length == 3 ? args[0] : 1.0
var from = args[args.length == 3 ? 1 : 0]
var to = args[args.length == 3 ? 2 : 1]

var url = 'http://finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=' + from.toUpperCase() + to.toUpperCase() + '=X'
var data = Utils.requestUrl(url)
console.log(data.split(',')[1] * amount)
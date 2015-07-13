var lang = 'en'
var words

assertInfo(args.length != 0,
	"Usage: \n" +
	" * wikipedia\n" +
	" * wikipedia en quantum mechanics\n" +
	" * wikipedia quantum mechanics"
)

if (args.length > 0 && (args[0] == 'en' || args[0] == 'pl')) {
	lang = args[0]
	words = args.slice(1)
}
else {
	words = args
}

var query = null
if (!!words && words.length > 0) {
	query = words.join(' ')
}

var url = 'https://' + lang + '.wikipedia.org'

if (query) {
	url = url + '/w/index.php?search=' + query
}
console.log(url)
Utils.openUrl(encodeURI(url))
console.hide()
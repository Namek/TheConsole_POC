var url = 'https://maps.google.pl'

if (args.length > 0) {
	url += '/maps?q=' + args.join(' ')
}
console.log("Opening " + url)
Utils.openUrl(encodeURI(url))
console.hide()
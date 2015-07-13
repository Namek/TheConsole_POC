var url = 'https://youtube.com';

if (args.length > 0) {
	url += '/results?search_query=' + args.join(' ');
}
console.log("Opening " + url);
Utils.openUrl(url); 
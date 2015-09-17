function downloadIt() {
	var libraryId = fixString(document.getElementsByName("libraryId")[0].value).toLowerCase().replace(/ /g, '').replace(/-/g, '_');
	var xmlFile = generateXmlFile();

	if(xmlFile != "") {
		xmlFile = unescapeHTML(xmlFile);
		download(xmlFile, 'library_' + libraryId + '_strings.xml', 'text/plain');
	}
}

function doSomeMagic() {
	var xmlFile = generateXmlFile();

	if(xmlFile != "") {
		document.getElementById("output").innerHTML = xmlFile;
		Prism.highlightAll();
		document.getElementById("output_container").removeAttribute("style");
	}
}

function generateXmlFile() {
	var libraryId = fixString(document.getElementsByName("libraryId")[0].value).toLowerCase().replace(/ /g, '').replace(/-/g, '_');

	var e = document.getElementsByName("isInternal")[0];
	var isInternal = e.options[e.selectedIndex].value;

	var author = fixString(document.getElementsByName("authorName")[0].value);
	var authorWebsite = fixString(document.getElementsByName("authorWebsite")[0].value);
	var libraryName = fixString(document.getElementsByName("libraryName")[0].value);
	var libraryDescription = fixString(document.getElementsByName("libraryDescription")[0].value);
	//var libraryDescription = document.getElementsByName("libraryDescription")[0].value;
	var libraryVersion = fixString(document.getElementsByName("libraryVersion")[0].value);
	var libraryWebsite = fixString(document.getElementsByName("libraryWebsite")[0].value);

	var e = document.getElementsByName("licenseId")[0];
	var licenseId = e.options[e.selectedIndex].value;

	var isOpenSource = fixString(document.getElementsByName("isOpenSource")[0].value);
	var repositoryLink = fixString(document.getElementsByName("repositoryLink")[0].value);
	var classPath = fixString(document.getElementsByName("classPath")[0].value);

	if(libraryId == '' || author == '' || libraryName == '') {
		alert("Please define an LibraryId, AuthorName and LibraryName first!");
	} else {
		var result =  '&lt;?xml version="1.0" encoding="utf-8"?>\n\n';
		result = result + '&lt;resources>' + '\n';
		result = result + '\t' + '&lt;string name="define_' + isInternal + libraryId + '">&lt;/string>' + '\n';
		result = result + '\t' + '&lt;!-- Author section -->' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_author">' + author + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_authorWebsite">' + authorWebsite + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;!-- Library section -->' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_libraryName">' + libraryName + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_libraryDescription">' + libraryDescription + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_libraryWebsite">' + libraryWebsite + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_libraryVersion">' + libraryVersion + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;!-- OpenSource section -->' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_isOpenSource">' + isOpenSource + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_repositoryLink">' + repositoryLink + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;!-- ClassPath for autoDetect section -->' + '\n';
		result = result + '\t' + '&lt;string name="library_' + libraryId + '_classPath">' + classPath + '&lt;/string>' + '\n';
		result = result + '\t' + '&lt;!-- License section -->' + '\n';
		if(licenseId == '') {
			result = result + '\t' + '&lt;string name="library_' + libraryId + '_licenseVersion">LICENSE-NAME&lt;/string>' + '\n';
			result = result + '\t' + '&lt;string name="library_' + libraryId + '_licenseLink">LICENSE-LINK&lt;/string>' + '\n';
			result = result + '\t' + '&lt;string name="library_' + libraryId + '_licenseContent">LICENSE-TEXT&lt;/string>' + '\n';
		} else {
			result = result + '\t' + '&lt;string name="library_' + libraryId + '_licenseId">' + licenseId + '&lt;/string>' + '\n';
		}
		result = result + '\t' + '&lt;!-- Custom variables section -->' + '\n';
		result = result + '&lt;/resources>' + '\n';

		return result;
	}
	return "";
}

function fixString(elem) {
	if(typeof elem == 'undefined') {
		return '';
	}
	return elem;
}

function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&amp;/g,'&');
}

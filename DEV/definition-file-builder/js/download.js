function download(strData, strFileName, strMimeType) {
	var D = document,
		a = D.createElement("a");
		strMimeType= strMimeType || "application/octet-stream";

	if (navigator.msSaveBlob) { // IE10+
		return navigator.msSaveBlob(new Blob([strData], {type: strMimeType}), strFileName);
	} /* end if(navigator.msSaveBlob) */



	if ('download' in a) { //html5 A[download]
		if(window.URL){
			a.href= window.URL.createObjectURL(new Blob([strData]));
			
 		}else{
			a.href = "data:" + strMimeType + "," + encodeURIComponent(strData);
		}
		a.setAttribute("download", strFileName);
		a.innerHTML = "downloading...";
		D.body.appendChild(a);
		setTimeout(function() {
			a.click();
			D.body.removeChild(a);
			if(window.URL){setTimeout(function(){ window.URL.revokeObjectURL(a.href);}, 250 );}
		}, 66);
		return true;
	} /* end if('download' in a) */

	
	//do iframe dataURL download (old ch+FF):
	var f = D.createElement("iframe");
	D.body.appendChild(f);
	f.src = "data:" +  strMimeType   + "," + encodeURIComponent(strData);

	setTimeout(function() {
		D.body.removeChild(f);
	}, 333);
	return true;
} /* end download() */
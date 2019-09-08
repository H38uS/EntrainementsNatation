function displayError(xhr, status, error) {
	var errorMessage = xhr.status + ': ' + xhr.statusText + " (" + error + ")";
	stopLoadingAnimation();
	alert('Une erreur est survenue - ' + errorMessage);
}

/** Stops a valid timer. */
function clearTimer(pTimeOut) {
	if (typeof pTimeOut === 'number') {
		window.clearTimeout(pTimeOut);
	}
}

//Fonctions de chargement
var timeoutId = null;
var $body = $("body");
function doTheLoading() {
	$body.addClass("loading");
}
function startLoadingAnimation() {
	clearTimer(timeoutId);
	timeoutId = window.setTimeout(doTheLoading, 400);
}
function stopLoadingAnimation() {
	clearTimer(timeoutId);
	$body.removeClass("loading");
}
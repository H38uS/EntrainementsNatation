function displayError(xhr, status, error) {
	var errorMessage = xhr.status + ': ' + xhr.statusText + " (" + error + ")";
	alert('Une erreur est survenue - ' + errorMessage);
}

/** Stops a valid timer. */
function clearTimer(pTimeOut) {
	if (typeof pTimeOut === 'number') {
		window.clearTimeout(pTimeOut);
	}
}
function displayError(xhr, status, error) {
	var errorMessage = xhr.status + ': ' + xhr.statusText + " (" + error + ")";
	alert('Une erreur est survenue - ' + errorMessage);
}
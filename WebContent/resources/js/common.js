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
	timeoutId = window.setTimeout(doTheLoading, 300);
}
function stopLoadingAnimation() {
	clearTimer(timeoutId);
	$body.removeClass("loading");
}

/**
 * Copy the next training text to the clip board.
 */
function copyText() {
	var button = $(this);
	var content = button.parent().parent().find("pre");
	
	if (window.clipboardData) { // For IE.
		window.clipboardData.setData("Text", content.text());
	} else {
		var tmpElem = $('<div>');
		tmpElem.css({position: "absolute", left: "-1000px", top: "-1000px"});
		tmpElem.append('<pre>' + content.text() + '</pre>');
		$("body").append(tmpElem);
		
		var range = document.createRange();
		range.selectNodeContents(tmpElem.get(0));
		
		var selection;
		selection = window.getSelection();
		selection.removeAllRanges();
		selection.addRange(range);

		success = document.execCommand ("copy", false, null);
		if (success) {
			button.attr("title", "Text copié !");
			button.tooltip('show');
			tmpElem.remove();
			setTimeout(() => {
				button.tooltip('hide');
			}, 3000);
		}
	}
}

/**
 * 
 * @param training The training json object.
 * @param canModify True if the user can modify the trainings.
 * @returns The training div.
 */
function getTrainingColDiv(training, canModify) {

	var trainingCol = $('<div></div>');
	trainingCol.addClass("col-12 col-xl-6 my-2");
	
	var trainingDiv = $('<div></div>');
	trainingDiv.addClass("p-3 bg-light rounded border border-dark h-100");
	
	var content = $("<pre></pre>");
	content.text(training.text);
	var contentContainer = $('<div class="pb-3"></div>');
	contentContainer.append(content);
	
	var par = typeof training.coach === 'undefined' ? "" : '<span class="badge badge-dark p-2 ml-1">' + training.coach.name + "</span>";
	var bassin = "";
	var isLongCourse = training.isCourseSizeDefinedForSure && training.isLongCourse;
	if (training.isCourseSizeDefinedForSure) {
		bassin = training.isLongCourse ? ' <span class="badge badge-warning p-2">Grand Bain</span>' : ' <span class="badge badge-primary p-2">Petit Bain</span>';
	}
	var size = '<span class="badge badge-info p-2">' + training.size + "m</span>";
	
	// Actions
	var actionDiv = $('<div class="text-right"></div>');
	var imgCopy = $('<img class="btn btn-light" data-toggle="tooltip" width="50px" src="resources/images/copy.png" />');
	imgCopy.click(copyText);
	actionDiv.append(imgCopy);
	if (canModify) {
		var imgEdit = $('<a href="modification/edit.jsp?id=' + training.id + '"></a>');
		imgEdit.append($('<img class="btn btn-light" width="50px" src="resources/images/my_edit.png" />'));
		actionDiv.append(imgEdit);
	}
	
	trainingDiv.append('<h5 class="text-center pb-1">' + training.dateSeanceString + "</h5>");
	trainingDiv.append(actionDiv);
	trainingDiv.append(contentContainer);
	trainingDiv.append('<div class="mt-3 text-right position-absolute p-right-corner">' + size + par + bassin + "</div>");
	
	trainingCol.append(trainingDiv);
	return trainingCol;
}

function formatDate(myDate) {
	return myDate.getFullYear() 
		+ "-"
		+ ("0" + (myDate.getMonth() + 1)).slice(-2)
		+ "-"
		+ ("0" + myDate.getDate()).slice(-2);
}
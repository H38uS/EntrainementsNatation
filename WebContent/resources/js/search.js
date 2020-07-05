var nextPageNumber = 1;

/** 
 * Recomputes the training area.
 * 
 * @param shouldReset true if we should clear the area before.
 * */
function loadMoreTrainings(shouldReset) {

	if (shouldReset) {
		nextPageNumber = 1;
		$("#resArea").text("");
		$("#btn-load-some-more").show();
	} else {
		nextPageNumber++;
	}
	
	startLoadingAnimation();
	$.get(  "public/service/search",
			{
				minsize: 	$("#minsize").val(),
				maxsize: 	$("#maxsize").val(),
				from: 		$("#from").val(),
				to:		 	$("#to").val(),
				order:	 	$("#order").val(),
				page:		nextPageNumber
			}
	).done(function (data) {
		
		var rawData = JSON.parse(data);
		var jsonData = rawData.message.trainings;
		var nbResultPerPage = rawData.message.maxResultPerPage;
		var total = rawData.message.totalNbOfResults;
		
		if (jsonData.length == 0) {
			var message = $("<div></div>");
			message.addClass("row alert alert-warning mt-2");
			if (shouldReset) {
				message.text("Aucun entrainement trouvé correspondant aux critères de recherche...");
			} else {
				message.text("Plus d'entrainements correspondant aux criètres de recherche !");
			}
			$("#resArea").append(message);
		}
		if (jsonData.length == 0 || jsonData.length < nbResultPerPage) {
			$("#btn-load-some-more").hide();
		}

		var row = $("<div></div>");
		row.addClass("row justify-content-start");
		$.each(jsonData, function(i, training) {
			var col = getTrainingColDiv(training, rawData.canModify, rawData.isAdmin);
			col.addClass("px-0 px-xl-1");
			col.hide().fadeIn();
			row.append(col);
		});
		$("#resArea").append(row);
		
		$("#info-nb-res").text("Affichage de " + ((nextPageNumber - 1) * nbResultPerPage + jsonData.length) + " / " + total + " entrainements");
		stopLoadingAnimation();
	})
	.fail(displayError);
}

/** Recomputes the training area. */
function refreshTrainings() {
	loadMoreTrainings(true);
}
/** Loads more training. */
function requestMore() {
	loadMoreTrainings(false);
}

// Affichage de la page, on affiche les entrainements
refreshTrainings();

// Déclenchement des rafraichissements
$("#minsize").change(refreshTrainings);
$("#maxsize").change(refreshTrainings);
$("#from").change(refreshTrainings);
$("#to").change(refreshTrainings);
$("#order").change(refreshTrainings);
$("#btn-rechercher").click(refreshTrainings);
$("#btn-load-some-more").click(requestMore);


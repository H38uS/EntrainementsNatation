var nextPageNumber = 1;

/**
 * 
 * @param training The training json object.
 * @returns The training div.
 */
function getTrainingColDiv(training) {

	var trainingCol = $('<div></div>');
	trainingCol.addClass("col-12 col-xl-6 my-2");
	
	var trainingDiv = $('<div></div>');
	trainingDiv.addClass("p-3 bg-light rounded border border-dark");
	
	var content = $("<pre></pre>");
	content.text(training.text);
	
	var par = typeof training.coach === 'undefined' ? "" : ", par " + training.coach.name;
	var bassin = "";
	var isLongCourse = training.isCourseSizeDefinedForSure && training.isLongCourse;
	if (training.isCourseSizeDefinedForSure) {
		bassin = training.isLongCourse ? ' <span class="badge badge-warning p-2">Grand Bain</span>' : ' <span class="badge badge-primary p-2">Petit Bain</span>';
	}
	
	trainingDiv.append('<h5 class="text-center pb-1">' + training.dateSeanceString + par + "</h5>");
	trainingDiv.append(content);
	trainingDiv.append('<div class="text-right"><span class="badge badge-info p-2">' + training.size + "m</span>" + bassin + "</div>");
	
	trainingCol.append(trainingDiv);
	return trainingCol;
}

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
		
		var row = null;
		var jsonData = JSON.parse(data);
		
		if (jsonData.length == 0) {
			var message = $("<div></div>");
			message.addClass("row alert alert-warning mt-2");
			if (shouldReset) {
				message.text("Aucun entrainement trouvé correspondant aux critères de recherche...");
			} else {
				message.text("Plus d'entrainements correspondant aux criètres de recherche !");
			}
			$("#btn-load-some-more").hide();
			$("#resArea").append(message);
		}
		
		$.each(jsonData, function(i, training) {
			if (i % 2 == 0) {
				row = $("<div></div>");
				row.addClass("row justify-content-start");
				// Append the current training block
				$("#resArea").append(row);
			}
			var margin = i % 2 == 0 ? "px-0 pl-xl-0 pr-xl-1" : "px-0 pr-xl-0 pl-xl-1";
			var col = getTrainingColDiv(training);
			col.addClass(margin);
			col.hide().fadeIn();
			row.append(col);
		});
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



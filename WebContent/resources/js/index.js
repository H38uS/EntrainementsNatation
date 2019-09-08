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
	
	trainingDiv.append('<div class="text-center pb-1">Réalisé le ' + training.dateSeance + par + "</div>");
	trainingDiv.append(content);
	trainingDiv.append('<div class="text-right"><span class="badge badge-info p-2">' + training.size + "m</span>" + bassin + "</div>");
	
	trainingCol.append(trainingDiv);
	return trainingCol;
}

/** Recomputes the training area */
function refreshTrainings() {
	startLoadingAnimation();
	$.get(  "public/service/search",
			{ minsize: $("#minsize").val(), maxsize: $("#maxsize").val() }
	).done(function (data) {
		$("#resArea").hide().text("");
		var row = null;
		$.each(JSON.parse(data), function(i, training) {
			if (i % 2 == 0) {
				row = $("<div></div>");
				row.addClass("row justify-content-start");
				// Append the current training block
				$("#resArea").append(row);
			}
			var margin = i % 2 == 0 ? "px-0 pl-xl-0 pr-xl-1" : "px-0 pr-xl-0 pl-xl-1";
			var col = getTrainingColDiv(training);
			col.addClass(margin);
			row.append(col);
		});
		stopLoadingAnimation();
		$("#resArea").fadeIn();
	})
	.fail(displayError);
}

// Affichage de la page, on affiche les entrainements
refreshTrainings();

// Déclenchement des rafraichissements
$("#minsize").change(refreshTrainings);
$("#maxsize").change(refreshTrainings);
$("#btn-rechercher").click(refreshTrainings);



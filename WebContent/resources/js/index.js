/** 
 * Computes the training area.
 */
function loadTrainings() {

	startLoadingAnimation();
	$.get(  "public/service/search",
			{
				order:	 	"date_seance desc, updatedAt desc",
				limite:		4,
				page:		1
			}
	).done(function (data) {
		
		var rawData = JSON.parse(data);
		var jsonData = rawData.message.trainings;
		var nbResultPerPage = rawData.message.maxResultPerPage;
		var total = rawData.message.totalNbOfResults;
		
		var row = $("<div></div>");
		row.addClass("row justify-content-start");
		$.each(jsonData, function(i, training) {
			var col = getTrainingColDiv(training, rawData.canModify, rawData.isAdmin);
			col.addClass("px-0 px-xl-1");
			col.hide().fadeIn();
			row.append(col);
		});
		$("#resArea").append(row);
		
		var isAdmin = rawData.isAdmin;
		if (isAdmin) {
			$("#menu_index").append('<div class="col-auto text-center"><a href="admin/admin.html" class="img"><img width="150px" src="resources/images/admin.png" /></a></div>');
		}
		
		stopLoadingAnimation();
	})
	.fail(displayError);
}

// Affichage de la page, on affiche les entrainements
loadTrainings();



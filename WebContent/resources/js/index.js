/** 
 * Computes the training area.
 */
function loadTrainings() {

	startLoadingAnimation();
	$.get(  "public/service/search",
			{
				order:	 	"date_seance desc",
				limite:		4,
				page:		1
			}
	).done(function (data) {
		
		var row = null;
		var rawData = JSON.parse(data);
		var jsonData = rawData.message.trainings;
		var nbResultPerPage = rawData.message.maxResultPerPage;
		var total = rawData.message.totalNbOfResults;
		
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



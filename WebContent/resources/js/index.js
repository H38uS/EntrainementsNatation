/** 
 * Computes the training area.
 */
function loadTrainings() {

    startLoadingAnimation();
    doGet(  "public/service/search",
            {
                order:	 	"date_seance desc, updatedAt desc",
                limite:		4,
                page:		1
            },
            function (resp) {

        var jsonData = resp.message.trainings;
        var nbResultPerPage = resp.message.maxResultPerPage;
        var total = resp.message.totalNbOfResults;

        var row = $("<div></div>");
        row.addClass("row justify-content-start");
        $.each(jsonData, function(i, training) {
            var col = getTrainingColDiv(training, resp.canModify, resp.isAdmin);
            col.addClass("px-0 px-xl-1");
            col.hide().fadeIn();
            row.append(col);
        });
        $("#resArea").append(row);

        var isAdmin = resp.isAdmin;
        if (isAdmin) {
            $("#menu_index").append('<div class="col-auto text-center"><a href="admin/admin.jsp" class="img"><img width="150px" src="resources/images/admin.png" /></a></div>');
        }
    });
}

/* Load a training from the bdd */
function loadRandomTraining() {
    startLoadingAnimation();
    doGet("public/service/randomtraining", {}, function(resp) {
        var col = getTrainingColDiv(resp.message, resp.canModify, resp.isAdmin);
        col.addClass("px-0 px-xl-1");
        $("#resAreaRandom").hide().html(col).fadeIn();
    });
}

// Affichage de la page, on affiche les entrainements
loadRandomTraining();
loadTrainings();

$("#btn-load-another-random").click(loadRandomTraining);

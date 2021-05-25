var nextPageNumber = 1;

/**
 * Recomputes the training area.
 *
 * @param shouldReset true if we should clear the area before.
 */
function loadMoreTrainings(shouldReset) {

    var showMeMore = $("#btn-load-some-more");
    showMeMore.parent().hide();

    if (shouldReset) {
        nextPageNumber = 1;
        $("#resArea").text("");
        $("#btn-load-some-more").show();
    } else {
        nextPageNumber++;
    }

    startLoadingAnimation();

    // Variables
    var minsize    = $("#minsize").val();
    var maxsize    = $("#maxsize").val();
    var order      = $("#order").val();

    doGet(  "public/service/search",
            {
                minsize:    minsize,
                maxsize:    maxsize,
                only_fav:   true,
                order:      order,
                page:       nextPageNumber
            },
            function (resp) {
                var jsonData = resp.message.trainings;
                var nbResultPerPage = resp.message.maxResultPerPage;
                var total = resp.message.totalNbOfResults;

                if (jsonData.length == 0) {
                    var message = $("<div></div>");
                    message.addClass("row alert alert-warning mt-2");
                    if (shouldReset) {
                        message.text("Aucun entrainement trouvé correspondant aux critères de recherche...");
                    } else {
                        message.text("Plus d'entrainements correspondant aux criètres de recherche !");
                    }
                    $("#resArea").append(message);
                } else if (nextPageNumber > 1) {
                    // Si on a encore des entrainements, on met une séparation
                    // We want to save the current position
                    var split = $(`<div id="split-${nextPageNumber}" class="row alert alert-info mb-0">Et hop! Plus d'entrainements ci-dessous...</div>`);
                    $("#resArea").append(split);
                }
                if (jsonData.length == 0 || jsonData.length < nbResultPerPage) {
                    $("#btn-load-some-more").hide();
                }

                var row = $("<div></div>");
                row.addClass("row justify-content-start");
                $.each(jsonData, function(i, training) {
                    var col = getTrainingColDiv(training, resp.canModify, resp.isAdmin);
                    col.addClass("px-0 px-xl-1");
                    col.hide().fadeIn();
                    row.append(col);
                });
                $("#resArea").append(row);

                if (jsonData.length > 0 && nextPageNumber > 1) {
                    var split = $("#split-" + nextPageNumber);
                    $("html").scrollTop(split.offset().top - 40);
                }

                $("#info-nb-res").text("Affichage de " + ((nextPageNumber - 1) * nbResultPerPage + jsonData.length) + " / " + total + " entrainements");
                showMeMore.parent().show();
            }
    );
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
// Après avoir vérifier si on avait pas une recherche enregistrée
refreshTrainings();

// Déclenchement des rafraichissements
$("#minsize").change(refreshTrainings);
$("#maxsize").change(refreshTrainings);
$("#order").change(refreshTrainings);
$("#btn-rechercher").click(refreshTrainings);

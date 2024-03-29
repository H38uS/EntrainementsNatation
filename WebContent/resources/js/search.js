var nextPageNumber = 1;

/** 
 * Recomputes the training area.
 * 
 * @param shouldReset true if we should clear the area before.
 */
function loadMoreTrainings(shouldReset) {

    var showMeMore = $("#btn-load-some-more");
    showMeMore.parent().hide();

    var isHidden = $("#order").val() === "rand()";
    if (shouldReset) {
        nextPageNumber = 1;
        $("#resArea").text("");
        $("#btn-load-some-more").toggle(!isHidden);
    } else {
        nextPageNumber++;
    }

    startLoadingAnimation();

    // Variables
    var minsize    = $("#minsize").val();
    var maxsize    = $("#maxsize").val();
    var from       = $("#from").val();
    var to         = $("#to").val();
    var coach      = $("#coach").val();
    var day        = $("#day").val();
    var only_fav   = $("#only-fav").is(":checked");
    var order      = $("#order").val();

    doGet(  "public/service/search",
            {
                minsize:    minsize,
                maxsize:    maxsize,
                from:       from,
                to:         to,
                coach:      coach,
                day:        day,
                only_fav:   only_fav,
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
                if (jsonData.length == 0 || jsonData.length < nbResultPerPage || isHidden) {
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

                // URL update
                ChangeUrl(`search.jsp?minsize=${minsize}&maxsize=${maxsize}&from=${from}&to=${to}&coach=${coach}&day=${day}&only-fav=${only_fav}&order=${order}`);
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

function initialLoading() {

    // On regarde si on a pas une recherche
    // Not using the library function as we want to know if the use is not logged in
    $.get(  "protected/service/search_criteria",
            { }
    ).done(function (data) {
        if (isUserNOTConnectedFromResponse(data)) {
            // The user is not connected - he cannot save/clear searches.
            $("#btn-save-search").hide();
            $("#btn-delete-search").hide();
        } else {
            var rawData = JSON.parse(data);
            var jsonData = rawData.message;
            if (jsonData && rawData.status === "OK") { // true, not null, not empty
                // we do have a saved search !
                $("#minsize").val(jsonData.minimalSize);
                $("#maxsize").val(jsonData.maximalSize);
                $("#from").val(jsonData.fromMonthInclusive);
                $("#to").val(jsonData.toMonthInclusive);
                if (typeof jsonData.coach !== 'undefined') {
                    $("#coach").val(jsonData.coach.name);
                }
                $("#day").val(jsonData.dayOfWeek);
            }
            // else on ignore
        }

        if (getURLParameter(window.location.search, 'minsize') > 0) {
            $("#minsize").val(getURLParameter(window.location.search, 'minsize'));
        }
        if (getURLParameter(window.location.search, 'maxsize') > 0) {
            $("#maxsize").val(getURLParameter(window.location.search, 'maxsize'));
        }
        if (getURLParameter(window.location.search, 'from')) {
            $("#from").val(getURLParameter(window.location.search, 'from'));
        }
        if (getURLParameter(window.location.search, 'to')) {
            $("#to").val(getURLParameter(window.location.search, 'to'));
        }
        if ($('#coach option').length > 1 && getURLParameter(window.location.search, 'coach')) {
            $("#coach").val(getURLParameter(window.location.search, 'coach'));
        }
        if (getURLParameter(window.location.search, 'day')) {
            $("#day").val(getURLParameter(window.location.search, 'day'));
        }
        $("#only-fav").prop( "checked", getURLParameter(window.location.search, 'only-fav') === "true");
        if (getURLParameter(window.location.search, 'order')) {
            $("#order").val(getURLParameter(window.location.search, 'order'));
        }

        refreshTrainings();
   }).fail(displayError);
}

function saveTheCriteria() {
    doPost(  "protected/service/search_criteria",
             {
                 minsize:    $("#minsize").val(),
                 maxsize:    $("#maxsize").val(),
                 from:       $("#from").val(),
                 to:         $("#to").val(),
                 coach:      $("#coach").val(),
                 day:        $("#day").val(),
             },
             resp => actionDone("Critères de recherche sauvegardés !")
    );
}

function deleteTheCriteria() {
    doDelete( "protected/service/search_criteria",
              {},
              resp => actionDone("Critère de recherche supprimés."));
}

// Chargement des entraineurs
loadCoaches();

// Affichage de la page, on affiche les entrainements
// Après avoir vérifier si on avait pas une recherche enregistrée
initialLoading();

// Déclenchement des rafraichissements
$("#minsize").change(refreshTrainings);
$("#maxsize").change(refreshTrainings);
$("#from").change(refreshTrainings);
$("#to").change(refreshTrainings);
$("#order").change(refreshTrainings);
$("#coach").change(refreshTrainings);
$("#day").change(refreshTrainings);
$("#only-fav").change(refreshTrainings);
$("#btn-rechercher").click(refreshTrainings);
$("#btn-load-some-more").click(requestMore);
$("#btn-save-search").click(saveTheCriteria);
$("#btn-delete-search").click(deleteTheCriteria);


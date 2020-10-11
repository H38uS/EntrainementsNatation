
/* ********************* */
/* *** Loading Stuff *** */
/* ********************* */

var lastModalOpened;
var loadingTimeout; // Time before we display the loading animation
var timer;

function closeModal() {
    if (typeof lastModalOpened != 'undefined') {
        // Au cas où une action soit déclenché depuis une modal
        lastModalOpened.modal('hide');
    }
    clearTimeout(loadingTimeout);
    clearTimeout(timer);
    $("#loading_message_container").hide();
    $("#loading_message_container").removeClass().addClass("container position-fixed");
}

function getHTMLPopUpMessage(image, message) {

    var row = $("<div></div>");
    var pic = $("<div></div>");
    pic.addClass("col-auto");
    pic.html('<img src="resources/images/' + image + '" width="30px" />');

    var mess = $("<div></div>");
    mess.addClass("col");
    mess.html(message);

    row.append(pic);
    row.append(mess);

    return row;
}

function doLoading(message) {
    closeModal();
    loadingTimeout = setTimeout(function() {
        $("#loading_message_container").addClass('alert alert-warning');
        $("#loading_message_div").html(getHTMLPopUpMessage("loading.gif", message).html());
        $("#loading_message_container").slideDown();
    }, 400);
}
function actionDone(message) {
    closeModal();
    $("#loading_message_container").addClass('alert alert-success');
    $("#loading_message_div").html(getHTMLPopUpMessage("ok.png", message).html());
    $("#loading_message_container").slideDown();
    timer = setTimeout(function() {
        $("#loading_message_container").fadeOut('slow');
    }, 5000);
}
function actionError(message) {
    closeModal();
    $("#loading_message_container").addClass('alert alert-danger');
    $("#loading_message_div").html(getHTMLPopUpMessage("ko.png", message).html());
    $("#loading_message_container").slideDown();
    timer = setTimeout(function() {
        $("#loading_message_container").fadeOut('slow');
    }, 5000);
}

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

/* ************************ */
/* *** Common functions *** */
/* ************************ */

/**
 * Copy the next training text to the clip board.
 */
function copyText() {
    var button = $(this);
    var content = button.parent().parent().find(".training-text");

    if (window.clipboardData) { // For IE.
        window.clipboardData.setData("Text", content.text());
    } else {
        var tmpElem = $('<div>');
        tmpElem.css({position: "absolute", left: "-1000px", top: "-1000px"});
        tmpElem.append('<span></span>');
        tmpElem.find('span').html(content.html());
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

function deleteTraining() {
    if (!confirm("Veux-tu vraiment supprimer cet entrainement?")) {
        return;
    }
    var button = $(this);
    var trainingId = button.attr("id").substr("admin-delete-".length);
    doDelete(   "admin/service/entrainement",
                { id: trainingId }
    ).done(function (data) {
        var training = button.closest("div.col-12");
        training.fadeOut();
    });
}

function refreshFavPicture(currentIMG, newOne, trainingId) {
    // HTML update
    currentIMG.wrap("<div>");
    var parent = currentIMG.parent();
    var img = $(getFavDiv(newOne, trainingId).html());
    parent.html(img);
    img.unwrap();
    // New action
    img.click(addRemoveFromFav);
}

// Returns true if the user is accessing a service without being connected.
function isUserNOTConnectedFromResponse(data) {
    return data.startsWith("<html");
}

// Add or remove the current training from our saved trainings.
function addRemoveFromFav() {

    // Parameters
    var currentIMG = $(this);
    var inFav = currentIMG.hasClass("isInMyFav");
    var trainingId = currentIMG.attr("id").substr("fav-".length);
    var newOne = !inFav;

    if (inFav) {
        startLoadingAnimation();
        doDelete(   "protected/service/saved_training",
                    {
                        trainingId : trainingId,
                    }
        ).done(function (data) {
            // TODO gérer si on se déconnectes entre temps...
            var rawData = JSON.parse(data);
            if (rawData.status !== "OK") {
                alert("Une erreur est survenue: " + rawData.message);
            } else {
                refreshFavPicture(currentIMG, newOne, trainingId);
            }
            stopLoadingAnimation();
        });
    } else {
        startLoadingAnimation();
        doPost(     "protected/service/saved_training",
                    {
                        trainingId : trainingId,
                    }
        ).done(function (data) {
            if (isUserNOTConnectedFromResponse(data)) {
                stopLoadingAnimation();
                var modalDiv = $(`
                    <div class="modal fade" id="empModal" role="dialog">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h4 class="modal-title">Vous devez être connecté pour réaliser cette action.</h4>
                                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                                </div>
                                <div class="modal-body">
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Annuler</button>
                                </div>
                            </div>
                        </div>
                    </div>
                `);
                // Modal content
                var inlineBody = $(data).find("div");
                var modalBody = modalDiv.find('.modal-body');
                modalBody.append(inlineBody);
                // Rooting actions
                modalBody.find("#submit").click(function(e) {
                    e.preventDefault();
                    doPost( "login", // FIXME : à déplacer/mutaliser dans le fichier rest.js
                            {
                                j_username : modalBody.find('#username').val(),
                                j_password : modalBody.find('#password').val(),
                                "remember-me" : modalBody.find('#remember-me').is(":checked") ? "on" : "off",
                            }
                    ).done(function (data) {
                        modalDiv.modal('hide');
                        var rawData = JSON.parse(data);
                        if (rawData.status !== "OK") {
                            alert("Une erreur est survenue: " + rawData.message);
                        } else {
                            refreshFavPicture(currentIMG, newOne, trainingId);
                        }
                        stopLoadingAnimation();
                    });
                });
                // Display Modal
                modalDiv.modal('show');
                return;
            }

            var rawData = JSON.parse(data);
            if (rawData.status !== "OK") {
                alert("Une erreur est survenue: " + rawData.message);
            } else {
                refreshFavPicture(currentIMG, newOne, trainingId);
            }
            stopLoadingAnimation();
        });
    }
}

function getFavDiv(isFav, trainingId) {
    var favPicture = isFav ? "resources/images/heart_full.png" : "resources/images/heart_empty.png";
    var favIMG = $(`
        <div>
            <img id="fav-${trainingId}" class="btn btn-light favIcon" width="50px" src="${favPicture}" />
        </div>
    `);
    favIMG.find("img").addClass(isFav ? "isInMyFav" : "isNotInMyFav");
    favIMG.find("img").attr("title", isFav ? "Supprimer de mes favoris" : "Ajouter à mes favoris");
    return favIMG;
}

/**
 * 
 * @param training  The training json object.
 * @param canModify True if the user can modify the trainings.
 * @param isAdmin   True if the user is an admin.
 * @returns The training div.
 */
function getTrainingColDiv(training, canModify, isAdmin) {

    // Layout
    var cardContent = $(`
        <div class="trainingDiv p-3 bg-light rounded border border-dark h-100">
            <table class="h-100 w-100">
                <tr>
                    <td valign="top">
                        <div class="training_main_content h-100">
                            <h5 class="text-center pb-1">${training.dateSeanceString}</h5>
                            <div class="text-right">
                                ${getFavDiv(training.isSavedByCurrentUser, training.id).html()}
                                <img id="copy-${training.id}" class="btn btn-light" data-toggle="tooltip" width="50px" src="resources/images/copy.png" />
                                <a id="modif-edit-${training.id}" class="img" href="modification/edit.jsp?id=${training.id}">
                                    <img class="btn btn-light" width="50px" src="resources/images/my_edit.png" />
                                </a>
                                <img id="admin-delete-${training.id}" class="btn btn-light" data-toggle="tooltip" width="50px" src="resources/images/delete.png" />
                            </div>
                            <span class="training-text"></span>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td valign="bottom">
                        <div class="training_footer text-right"></div>
                    </td>
                </tr>
            </table>
        </div>`);

    // Footer, action et contenu de la séance
    var footer = cardContent.find(".training_footer");
    cardContent.find(".training-text").html(training.htmlText);
    cardContent.find(".training-text").find("p").last().addClass("mb-0");
    cardContent.find(`#copy-${training.id}`).click(copyText);
    if (!canModify) {
        cardContent.find(`#modif-edit-${training.id}`).remove();
    }
    if (isAdmin) {
        cardContent.find(`#admin-delete-${training.id}`).click(deleteTraining);
    } else {
        cardContent.find(`#admin-delete-${training.id}`).remove();
    }
    cardContent.find(".favIcon").click(addRemoveFromFav);

    // Contenu du footer
    var par = typeof training.coach === 'undefined' ? "" : '<span class="badge badge-dark p-2 mt-2 ml-1">' + training.coach.name + "</span>";
    var bassin = "";
    var isLongCourse = training.isCourseSizeDefinedForSure && training.isLongCourse;
    var tailleBassinText = training.isLongCourse ? "Grand Bain" : "Petit Bain";
    var tailleBassinClass = training.isLongCourse ? "badge-warning" : "badge-primary";
    if (training.isCourseSizeDefinedForSure) {
        bassin = ` <span class="badge ${tailleBassinClass} p-2 mt-2">${tailleBassinText}</span>`;
    }
    var size = `<span class="badge badge-info p-2 mt-2">${training.size}m</span>`;
    // Matos
    var requiresPull = typeof training.requiresPull != 'undefined' && training.requiresPull;
    var requiresPlaques = typeof training.requiresPlaques != 'undefined' && training.requiresPlaques;
    var requiresPalmes = typeof training.requiresPalmes != 'undefined' && training.requiresPalmes;
    var matos = "";
    if (requiresPull || requiresPlaques || requiresPalmes) {
        var matosText = "";
        if (requiresPull) {
            matosText = "pull ";
        }
        if (requiresPlaques) {
            matosText += "plaques ";
        }
        if (requiresPalmes) {
            matosText += "palmes ";
        }
        matosText.trim();
        matos = `<span class="badge badge-success p-2 ml-1 mt-2">${matosText}</span>`;
    }
    footer.html(size + par + bassin + matos);
    if (isAdmin && typeof training.createdBy != 'undefined') {
        footer.append(` <div class="font-italic">Ajouté par ${training.createdBy.email}, le ${training.createdAt}.</div>`);
    }

    // Main div
    var trainingCol = $('<div></div>');
    trainingCol.addClass("col-12 col-xl-6 my-2");
    trainingCol.append(cardContent);
    return trainingCol;
}

function formatDate(myDate) {
    return myDate.getFullYear()
        + "-"
        + ("0" + (myDate.getMonth() + 1)).slice(-2)
        + "-"
        + ("0" + myDate.getDate()).slice(-2);
}

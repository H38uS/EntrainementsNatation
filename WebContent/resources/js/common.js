/* ************************ */
/* *** Common functions *** */
/* ************************ */

function loadCoaches() {
    doGet("public/service/coach", {}, function (resp) {
        $.each(resp.message, function(i, coach) {
            $('#coach').append('<option value="' + coach.name + '">' + coach.name + '</option>');
        });
    });
}

function getURLParameter(url, name) {
    var results = new RegExp('[\?&]' + name + '=([^&]*)').exec(url);
    if (results == null) {
        return null;
    } else {
        return results[1].replaceAll('%20', ' ') || 0;
    }
}

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
                { id: trainingId },
                function(resp) {
                    actionDone("L'entrainement a bien été supprimé.");
                    var training = button.closest("div.col-12");
                    training.fadeOut();
                }
    );
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
                    },
                    function(resp) {
                        refreshFavPicture(currentIMG, newOne, trainingId);
                        actionDone("L'entrainement a bien été supprimé de vos favoris.");
                    }
        );
    } else {
        doPost( "protected/service/saved_training",
                {
                    trainingId : trainingId,
                },
                function(resp) {
                    refreshFavPicture(currentIMG, newOne, trainingId);
                    actionDone("L'entrainement a bien été enregistré dans vos favoris.");
                }
        );
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
                                <a id="open-${training.id}" class="img" href="open.jsp?id=${training.id}">
                                    <img class="btn btn-light" width="50px" src="resources/images/open.png" />
                                </a>
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

const stateTitle = '';
function ChangeUrl(url) {
    if (typeof (history.pushState) != "undefined") {
        var obj = { Title: stateTitle, Url: url };
        history.pushState(obj, stateTitle, obj.Url);
    } // else do nothing - we won't have the correct url and that's fine
}

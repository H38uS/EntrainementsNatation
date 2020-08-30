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
    $.ajax({
        url: "admin/service/entrainement",
        type: "DELETE",
        data: { id: trainingId, },
    }).done(function (data) {
        var training = button.closest("div.col-12");
        training.fadeOut();
    })
    .fail(displayError);
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

function loadCoaches() {
    $.get("public/service/coach")
        .done(function (data) {
            var resp = JSON.parse(data);
            if (resp.status === 'OK') {
                $.each(resp.message, function(i, coach) {
                    $('#coach').append('<option value="' + coach.name + '">' + coach.name + '</option>');
                });
            }
        })
        .fail(displayError);
}
/* ****************************** */
/* *** Loading & Status Stuff *** */
/* ****************************** */

var lastModalOpened;
var loadingTimeout; // Time before we display the loading animation
var timer;

/** Stops a valid timer. */
function clearTimer(pTimeOut) {
    if (typeof pTimeOut === 'number') {
        window.clearTimeout(pTimeOut);
    }
}

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

/* ********************** */
/* *** Rest functions *** */
/* ********************** */

function doGet(url, data = {}) {
    return $.get(url, data).fail(displayError);
}

function doPost(url, data = {}) {
    return $.post(url, data).fail(displayError);
}

function doDelete(url, data = {}) {
    return $.ajax({ url: url, type: "DELETE", data: data}).fail(displayError);
}

function doPut(url, data = {}) {
    startLoadingAnimation();
    return $.ajax(
        { url: url, type: "PUT", data: data}
    ).fail(displayError).done(function (data) {
        var resp = JSON.parse(data);
        if (resp.status === "OK") {
            actionDone(resp.message)
        } else {
            actionError(resp.message)
        }
        stopLoadingAnimation();
    });
}

function loadCoaches() {
    doGet("public/service/coach")
        .done(function (data) {
            var resp = JSON.parse(data);
            if (resp.status === 'OK') {
                $.each(resp.message, function(i, coach) {
                    $('#coach').append('<option value="' + coach.name + '">' + coach.name + '</option>');
                });
            }
        });
}
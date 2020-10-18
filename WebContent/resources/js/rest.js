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

// Returns true if the user is accessing a service without being connected.
function isUserNOTConnectedFromResponse(data) {
    return data.startsWith("<html");
}

// Handles the response received by the server and check if a login is required.
function handleResponse(responseData, successFunction, errorFunction) {
    // Do we require a login step?
    if (isUserNOTConnectedFromResponse(responseData)) {
        // Login required
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
        var inlineBody = $(responseData).find("div");
        var modalBody = modalDiv.find('.modal-body');
        modalBody.append(inlineBody);
        // Rooting actions
        modalBody.find("#submit").click(function(e) {
            e.preventDefault();
            // JQuery post to avoid circular references
            $.post( "login",
                    {
                        j_username : modalBody.find('#username').val(),
                        j_password : modalBody.find('#password').val(),
                        "remember-me" : modalBody.find('#remember-me').is(":checked") ? "on" : "off",
                    }
            ).done(function (data) {
                modalDiv.modal('hide');
                var resp = JSON.parse(data);
                if (resp.status === "OK") {
                    successFunction(resp);
                } else {
                    errorFunction(resp);
                }
            });
        });
        // Display Modal
        modalDiv.modal('show');
    } else {
        // No login required
        var resp = JSON.parse(responseData);
        if (resp.status === "OK") {
            successFunction(resp);
        } else {
            errorFunction(resp);
        }
    }
}

function doGet(url, data = {}) {
    return $.get(url, data).fail(displayError);
}

// url : the site url to call
// data : the form data
// successFunction(jsonResponse) : default success function.
// errorFunction(jsonResponse) : default error function is to notify about the error, can be overridden.
function doPost(url,
                data = {},
                successFunction = function(resp) {
                    actionDone("Action réalisée avec succès !");
                },
                errorFunction = function(resp) {
                    actionError(resp.message);
                }) {
    startLoadingAnimation();
    return $.post(url, data).fail(displayError).done(function (data) {
        handleResponse(data, successFunction, errorFunction);
        stopLoadingAnimation();
    });
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
            actionDone(resp.message);
        } else {
            actionError(resp.message);
        }
        stopLoadingAnimation();
    });
}
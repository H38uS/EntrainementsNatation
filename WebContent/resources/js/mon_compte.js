function loadSavedTrainings() {

    startLoadingAnimation();
    var resArea = $("#savedTrainingPlaceholder");
    resArea.empty();

    doGet("protected/service/saved_training", {}, function (resp) {

        var jsonData = resp.message;

        if (jsonData.length == 0) {
            var message = $("<div></div>");
            message.addClass("row alert alert-warning mt-2");
            message.text("Aucun entrainement enregistré pour le moment.");
            resArea.append(message);
            stopLoadingAnimation();
            return;
        }

        var row = $("<div></div>");
        row.addClass("row justify-content-start");
        $.each(jsonData, function(i, training) {
            var col = getTrainingColDiv(training, resp.canModify, resp.isAdmin);
            col.addClass("px-0 px-xl-1");
            col.hide().fadeIn();
            row.append(col);
        });
        resArea.append(row);
    });
}

loadSavedTrainings();
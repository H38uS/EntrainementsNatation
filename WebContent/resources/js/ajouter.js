loadCoaches();

// Init de la date
var today = formatDate(new Date());
$('#trainingdate').val(today);

$("#training").change(function () {
    $("#trainingSizeResult").hide();
    doGet(  "modification/service/trainingsize",
            { training: $(this).val() },
            function (resp) {
        $("#trainingSizeResult").text(resp.message).fadeIn();
        $("#trainingSizeCopy").removeClass("d-none");
    });
});

// Ajout d'un nouvel entrainement
function ajouter(e, forceTheCreation = false) {
    doPost( "modification/service/entrainement",
            {
                training:       $("#training").val(),
                size:           $("#size").val(),
                trainingdate:   $("#trainingdate").val(),
                coach:          $("#coach option:selected").val(),
                force:          forceTheCreation,
                poolsize:       $('input[name=poolsize]:checked').val()
            },
            resp => actionDone("L'entrainement a bien été ajouté."),
            function (resp) { // error function
                if (resp.message.includes("Un entrainement existe le m")) {
                    // Obligé de remettre le message sinon contient un <li> bizarre
                    if (confirm("Un entrainement existe le même jour, avec la même taille et le même entraineur. Voulez-vous quand même ajouter celui-ci ?")) {
                        ajouter(e, true);
                        return;
                    } else {
                        actionError("L'entrainement n'a pas été ajouté.");
                    }
                } else {
                    actionError(resp.message);
                }
            }
    );
}

const sizeRegex = /[0-9]+/;
function copyTrainingSize(e) {
    e.preventDefault();
    var size = $("#trainingSizeResult").text().match(sizeRegex);
    $("#size").val(size);
}

$("#trainingSizeCopyButton").click(copyTrainingSize);
$("#ajouter").click(ajouter);
loadCoaches();

// Init de la date
var today = formatDate(new Date());
$('#trainingdate').val(today);

$("#training").change(function () {
    $("#trainingSizeResult").hide();
    doGet(  "modification/service/trainingsize",
            { training: $(this).val() }
    ).done(function (data) {
        var resp = JSON.parse(data);
        var text = "";
        if (resp.status === "OK") {
            text = resp.message;
        } else {
            text = "Une erreur est survenue: " + resp.message;
        }
        $("#trainingSizeResult").text(text).fadeIn();
    });
});

// Ajout d'un nouvel entrainement
function ajouter(force = false) {

    doPost( "modification/service/entrainement",
            {
                training: 		$("#training").val(),
                size: 			$("#size").val(),
                trainingdate:	$("#trainingdate").val(),
                coach:			$("#coach option:selected").val(),
                force:          force,
                poolsize:		$('input[name=poolsize]:checked').val()
            }
    ).done(function (data) {
        var resp = JSON.parse(data);
        if (resp.status !== "OK") {
            if (resp.message.includes("Un entrainement existe le même jour, avec la même taille et le même entra")) {
                if (confirm("Un entrainement existe le même jour, avec la même taille et le même entraineur. Voulez-vous quand même ajouter celui-ci ?")) {
                    ajouter(true);
                    return;
                }
            }
        }
    });
}

$("#ajouter").click(ajouter);
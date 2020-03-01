
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

// Init de la date
var today = formatDate(new Date());
$('#trainingdate').val(today);

$("#training").change(function () {
	$("#trainingSizeResult").hide();
	$.get(  "modification/service/trainingsize",
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
	})
	.fail(displayError);
});

// Ajout d'un nouvel entrainement
function ajouter(force = false) {

	clearTimer(feedbackTimeout);
	$("#ajouter_feedback").hide();

	startLoadingAnimation();
	$.post( "modification/service/entrainement",
			{
				training: 		$("#training").val(),
				size: 			$("#size").val(),
				trainingdate:	$("#trainingdate").val(),
				coach:			$("#coach option:selected").val(),
				force:          force,
				poolsize:		$('input[name=poolsize]:checked').val()
			}
	).done(function (data) {
		$("#ajouter_feedback").removeClass();
		var resp = JSON.parse(data);
		if (resp.status === "OK") {
			$("#ajouter_feedback").addClass("alert alert-success mt-2");
			$("#ajouter_feedback").text(resp.message);
			clearTimer(feedbackTimeout);
			feedbackTimeout = window.setTimeout(function () {
				$("#ajouter_feedback").fadeOut();
			}, 5000);
		} else {
		    if (resp.message.includes("Un entrainement existe le même jour, avec la même taille et le même entra")) {
                if (confirm("Un entrainement existe le même jour, avec la même taille et le même entraineur. Voulez-vous quand même ajouter celui-ci ?")) {
                    ajouter(true);
                    return;
                }
		    }
			$("#ajouter_feedback").addClass("alert alert-danger mt-2");
			$("#ajouter_feedback").html(resp.message);
		}
		stopLoadingAnimation();
		$("#ajouter_feedback").fadeIn();
	})
	.fail(displayError);
}

var feedbackTimeout = null;
$("#ajouter").click(ajouter);
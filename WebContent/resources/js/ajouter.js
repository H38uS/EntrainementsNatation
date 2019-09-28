
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
var now = new Date();
var today = now.getFullYear() + "-" + ("0" + (now.getMonth() + 1)).slice(-2) + "-" + ("0" + now.getDate()).slice(-2);
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
var feedbackTimeout = null;
$("#ajouter").click(function() {

	clearTimer(feedbackTimeout);
	$("#ajouter_feedback").hide();
	
	$.post( "modification/service/entrainement",
			{
				training: 		$("#training").val(),
				size: 			$("#size").val(),
				trainingdate:	$("#trainingdate").val(),
				coach:			$("#coach option:selected").val(),
				poolsize:		$("#poolsize option:selected").val()
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
			$("#ajouter_feedback").addClass("alert alert-danger mt-2");
			$("#ajouter_feedback").html(resp.message);
		}
		$("#ajouter_feedback").fadeIn();
	})
	.fail(displayError);
});
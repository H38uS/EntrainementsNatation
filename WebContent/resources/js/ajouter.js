
$.get("public/service/coach")
	.done(function (data) {
		$.each(JSON.parse(data), function(i, coach) {
			$('#coach').append('<option value="' + coach.name + '">' + coach.name + '</option>');
		});
	})
	.fail(displayError);

// Init de la date
var now = new Date();
var today = now.getFullYear() + "-" + ("0" + (now.getMonth() + 1)).slice(-2) + "-" + ("0" + now.getDate()).slice(-2);
$('#trainingdate').val(today);

$("#training").change(function () {
	$("#trainingSizeResult").hide();
	$.get(  "public/service/trainingsize",
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
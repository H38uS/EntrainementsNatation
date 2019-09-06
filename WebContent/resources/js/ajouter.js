$("#training").change(function () {
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
		$("#trainingSizeResult").text(text);
	})
	.fail(displayError);
});
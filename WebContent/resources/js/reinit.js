function displayMessage(isError, message) {
	var alertClass = isError ? "alert-danger" : "alert-success";
	$("#forget_invalid_feedback").addClass(alertClass);
	$("#forget_invalid_feedback").text(message);
	$("#forget_invalid_feedback").fadeIn();
}

function askForPasswordReinit() {

	// Init
	$("#forget_invalid_feedback").removeClass("alert-success");
	$("#forget_invalid_feedback").removeClass("alert-danger");
	$("#forget_invalid_feedback").hide();
	
	var email = $("#forget_username").val();
	if (email == "") {
		displayMessage(true, "Veuillez d'abord entrer une adresse email.");
		return;
	}
	
	if (!email.includes("@") || !email.includes(".")) {
		displayMessage(true, "L'addresse email ne semble pas correcte... Veuillez revérifier.");
		return;
	}
	
	startLoadingAnimation();
	$.post( "public/service/reinit_mdp",
			{
				email : email,
			}
	).done(function (data) {
		var rawData = JSON.parse(data);
		if (rawData.status !== "OK") {
			displayMessage(true, rawData.message);
		} else {
			displayMessage(false, "Un email a été envoyé à " 
					+ email 
					+ ". Cliquez sur le lien dans l'email pour réinitialiser votre mot de passe !");
		}
	}).fail(displayError);
}

$("#forget_submit").click(askForPasswordReinit);
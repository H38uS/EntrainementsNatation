/**
 * Post a request to create the account.
 */
function createAccount() {

	$("#creer_feedback").hide();
	startLoadingAnimation();
	$.post(  "public/service/creation_compte",
			{
				j_username: 			$("#c_username").val(),
				j_password: 			$("#c_password").val(),
				g_recaptcha_response: 	$("#g-recaptcha-response").val()
			}
	).done(function (data) {

		$("#creer_feedback").removeClass();
		var resp = JSON.parse(data);
		if (resp.status === "OK") {
			$("#creer_feedback").addClass("alert alert-success mt-2");
			$("#creer_feedback").text(resp.message);
			$("#creer_feedback").text("Utilisateur créé avec succès ! Vous pouvez maintenant vous connecter ci-dessus.");
		} else {
			$("#creer_feedback").addClass("alert alert-danger mt-2");
			var errorMessage = 'Des erreurs sont survenues, veuillez controler les données.<ul class="mb-0">';
			resp.message.forEach(item => errorMessage += "<li>" + item + "</li>");
			errorMessage += "</ul>";
			$("#creer_feedback").html(errorMessage);
		}

		$("#creer_feedback").fadeIn();
		stopLoadingAnimation();
	})
	.fail(displayError);
}

$("#c_submit").click(createAccount);
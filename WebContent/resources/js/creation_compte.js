/**
 * Post a request to create the account.
 */
function createAccount() {
    doPost(  "public/service/creation_compte",
            {
                j_username: 			$("#c_username").val(),
                j_password: 			$("#c_password").val(),
                g_recaptcha_response: 	$("#g-recaptcha-response").val()
            },
            resp => actionDone("Utilisateur créé avec succès ! Vous pouvez maintenant vous connecter ci-dessus."),
            function (resp) {
                var errorMessage = 'Des erreurs sont survenues, veuillez controler les données.<ul class="mb-0">';
                resp.message.forEach(item => errorMessage += "<li>" + item + "</li>");
                errorMessage += "</ul>";
                actionError(errorMessage);
            }
    );
}

$("#c_submit").click(createAccount);
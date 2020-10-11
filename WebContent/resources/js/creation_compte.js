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
            "Utilisateur créé avec succès ! Vous pouvez maintenant vous connecter ci-dessus."
    ).done(function (data) {
        var resp = JSON.parse(data);
        if (resp.status !== "OK") {
            var errorMessage = 'Des erreurs sont survenues, veuillez controler les données.<ul class="mb-0">';
            resp.message.forEach(item => errorMessage += "<li>" + item + "</li>");
            errorMessage += "</ul>";
            actionError(errorMessage);
        }
    });
}

$("#c_submit").click(createAccount);
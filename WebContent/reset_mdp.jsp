<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta charset="UTF-8">
        <link rel="shortcut icon" href="resources/images/nageur.ico" />
        <title>Entrainements Natation NC Alp'38</title>
        <base href="/ncalp38/">
        <link rel="stylesheet" type="text/css" href="resources/css/vendor/bootstrap.min-4.1.3.css" />
        <link rel="stylesheet" type="text/css" href="resources/css/vendor/jquery-ui.min.css" />
        <link rel="stylesheet" type="text/css" href="resources/css/common.css" />
    </head>
    <body>
        <div class="container mt-3">
            <h4>Réinitialisation du mot de passe</h4>
            <div class="form-group">
                    <label for="newpassword">Nouveau mot de passe</label>
                    <input type="password" class="form-control" id="newpassword" placeholder="Nouveau mot de passe">
            </div>
            <div class="form-group">
                    <label for="newpassword2">Confirmez votre mot de passe</label>
                    <input type="password" class="form-control" id="newpassword2" placeholder="Retapez le mot de passe">
            </div>
            <input id="p_user_id" type="hidden" value='${param["user_id"]}' />
            <input id="p_token" type="hidden" value='${param["token"]}' />

            <div id="submit_button" class="btn btn-primary">Modifier !</div>
            <div id="reinit_invalid_feedback" class="alert mt-3"></div>

            <div class="text-center">
                <a href="index.html" class="img" title="Retour à l'accueil">
                    <img width="100px" src="resources/images/home.png" />
                </a>
                <a href="search.html" class="img" title="Rechercher un entrainement">
                    <img width="100px" src="resources/images/search.png" />
                </a>
                <a href="modification/ajouter.html" class="img" title="Ajouter des entrainements">
                    <img width="100px" src="resources/images/add.png" />
                </a>
            </div>

            <!-- Scripts -->
            <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
            <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
            <script src="resources/js/rest.js" type="text/javascript"></script>
            <script src="resources/js/common.js" type="text/javascript"></script>
            <script type="text/javascript">

                function displayMessage(isError, message) {
                    var alertClass = isError ? "alert-danger" : "alert-success";
                    $("#reinit_invalid_feedback").addClass(alertClass);
                    $("#reinit_invalid_feedback").text(message);
                    $("#reinit_invalid_feedback").fadeIn();
                }

                function changePwd() {

                    // Init
                    $("#reinit_invalid_feedback").removeClass("alert-success");
                    $("#reinit_invalid_feedback").removeClass("alert-danger");
                    $("#reinit_invalid_feedback").hide();

                    var newpwd = $("#newpassword").val();
                    var newpwd2 = $("#newpassword2").val();

                    if (newpwd.length == 0 || newpwd2.length == 0) {
                        displayMessage(true, "Veuillez remplir les deux champs.");
                        return;
                    }
                    if (newpwd.length < 8) {
                        displayMessage(true, "Le mot de passe doit faire au moins 8 caractères.");
                        return;
                    }
                    if (newpwd !== newpwd2) {
                        displayMessage(true, "Les deux mots de passe ne correspondent pas.");
                        return;
                    }

                    startLoadingAnimation();
                    doPost(  "public/service/new_mdp_from_reinit",
                            {
                                user_id: 		$("#p_user_id").val(),
                                token: 			$("#p_token").val(),
                                newpassword: 	newpwd,
                                newpassword2:	newpwd2,
                            }
                    ).done(function (data) {
                        var rawData = JSON.parse(data);
                        if (rawData.status !== "OK") {
                            displayMessage(true, rawData.message);
                        } else {
                            displayMessage(false, "Mot de passe réinitialisé avec succès !");
                        }
                    });
                }
                $("#submit_button").click(changePwd);
            </script>
        </div>
    </body>
</html>
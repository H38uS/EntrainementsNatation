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
        <div class="container">
            <div class="container position-fixed mt-2" style="z-index:9999">
                <div id="loading_message_container" class="container row">
                    <div id="loading_message_div" class="row align-items-center"></div>
                </div>
            </div>
            <div>
                <div class="mt-2 text-right">
                    <a href="index.jsp" class="img" title="Retour à l'accueil">
                        <img width="50px" src="resources/images/home.png" />
                    </a>
                    <a href="search.html" class="img" title="Retour à la recherche d'entrainement">
                        <img width="50px" src="resources/images/search.png" />
                    </a>
                </div>
                <h4 class="">Modifier un entrainement</h4>
                <div class="alert alert-info">
                    Les entrainements sont affichés en utilisant le format <a href="https://commonmark.org/">markdown</a> (légèrement étendu).
                    <ul class="mb-0">
                        <li>Allez voir le <a href="https://commonmark.org/help/tutorial/index.jsp">tutoriel complet</a></li>
                        <li>Ou la liste des <a href="https://commonmark.org/help/">fonctionnalités de base</a></li>
                    </ul>
                </div>
                <div class="form-group">
                    <label for="training">Contenu de l'entrainement</label>
                    <textarea id="training" class="form-control" name="training" rows="15"></textarea>
                </div>
                <div class="form-group">
                    <label for="size">Taille de l'entrainement, en mètres. Indice: <span id="trainingSizeResult">...</span></label>
                    <input id="size" class="form-control" name="size" type="text" />
                </div>
                <div class="form-group">
                    <label for="trainingdate">Date de la séance</label>
                    <input id="trainingdate"
                            class="form-control"
                            type="date"
                            name="trainingdate"
                            placeholder="aaaa-mm-jj"
                            title="Utilisez le format suivant: aaaa-mm-jj (année sur 4 chiffres, tiret, mois sur 2 chiffres, tiret, jour sur deux chiffres)">
                </div>
                <div class="form-group">
                    <label for="coach">Entraineur</label>
                    <select id="coach" class="form-control" name="coach">
                        <option value=""></option>
                    </select>
                </div>
                <div class="form-group">
                    <div class="custom-control custom-radio custom-control-inline">
                        <input type="radio" id="radioPetitBac" name="poolsize" class="custom-control-input" value="short">
                        <label class="custom-control-label" for="radioPetitBac">Petit bassin</label>
                    </div>
                    <div class="custom-control custom-radio custom-control-inline">
                        <input type="radio" id="radioGrandBac" name="poolsize" class="custom-control-input" value="long">
                        <label class="custom-control-label" for="radioGrandBac">Grand bassin</label>
                    </div>
                </div>
                <input id="training_id" type="hidden" value='${param["id"]}' />
                <div class="text-center mt-2">
                    <button id="modifier" class="btn btn-primary" type="submit">Modifier</button>
                </div>
                <div class="text-right">
                    <a href="index.jsp" class="img" title="Retour à l'accueil">
                        <img width="50px" src="resources/images/home.png" />
                    </a>
                    <a href="search.html" class="img" title="Retour à la recherche d'entrainement">
                        <img width="50px" src="resources/images/search.png" />
                    </a>
                </div>
            </div>
        </div>
        <div class="my_modal"></div>

        <!-- Scripts -->
        <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
        <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
        <script src="resources/js/rest.js" type="text/javascript"></script>
        <script src="resources/js/common.js" type="text/javascript"></script>
        <script type="text/javascript">
            function computeTrainingSize() {
                $("#trainingSizeResult").hide();
                doGet(  "modification/service/trainingsize",
                        { training: $("#training").val() },
                        function(resp) {
                    $("#trainingSizeResult").text(resp.message).fadeIn();
                });
            }
            $("#training").change(computeTrainingSize);

            doGet("public/service/training", { id: $("#training_id").val()}, function(resp) {

                $("#training").val(resp.message.smileyText);
                computeTrainingSize();
                $("#size").val(resp.message.size);
                if (resp.message.dateSeance.length == 10) {
                    $("#trainingdate").val(resp.message.dateSeance);
                }

                doGet("public/service/coach", {}, function(coaches) {
                    $.each(coaches.message, function(i, coach) {
                        $('#coach').append('<option value="' + coach.name + '">' + coach.name + '</option>');
                    });
                    if (typeof resp.message.coach !== 'undefined') {
                        $('#coach option[value="' + resp.message.coach.name + '"]').prop('selected', true);
                    }
                });

                if (resp.message.isCourseSizeDefinedForSure) {
                    if (resp.message.isLongCourse) {
                        $('#radioGrandBac').prop('checked', true);
                    } else {
                        $('#radioPetitBac').prop('checked', true);
                    }
                }
            });

            // Ajout d'un nouvel entrainement
            $("#modifier").click(function() {
                doPut( "modification/service/entrainement",
                       {
                            id:				$("#training_id").val(),
                            training: 		$("#training").val(),
                            size: 			$("#size").val(),
                            trainingdate:	$("#trainingdate").val(),
                            coach:			$("#coach option:selected").val(),
                            poolsize:		$('input[name=poolsize]:checked').val(),
                });
            });
        </script>
    </body>
</html>
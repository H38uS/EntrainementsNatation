<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
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
        <t:menu />
        <div class="container pb-3">
            <div class="container position-fixed mt-2" style="z-index:9999">
                <div id="loading_message_container" class="container row">
                    <div id="loading_message_div" class="row align-items-center"></div>
                </div>
            </div>
            <div>
                <h4 class="mt-3">Ajouter un entrainement</h4>
                <div class="alert alert-info">
                    Les entrainements sont affichés en utilisant le format <a href="https://commonmark.org/">markdown</a> (légèrement étendu).
                    <ul class="mb-0">
                        <li>Allez voir le <a href="https://commonmark.org/help/tutorial/">tutoriel complet</a></li>
                        <li>Ou la liste des <a href="https://commonmark.org/help/">fonctionnalités de base</a></li>
                    </ul>
                </div>
                <div class="form-group">
                    <label for="training">Contenu de l'entrainement</label>
                    <textarea id="training" class="form-control" name="training" rows="15"></textarea>
                </div>
                <div class="form-group">
                    <label for="size">
                        Taille de l'entrainement, en mètres. Indice:
                        <span id="trainingSizeResult">...</span>
                        <span id="trainingSizeCopy" class="d-none">
                            <a id="trainingSizeCopyButton" href="">C'est ça!</a>
                        </span>
                    </label>
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
                <div class="text-center mt-2">
                    <button id="ajouter" class="btn btn-primary" type="submit">Ajouter</button>
                </div>
            </div>
        </div>
        <div class="my_modal"></div>

        <!-- Scripts -->
        <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
        <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
        <script src="resources/js/rest.js" type="text/javascript"></script>
        <script src="resources/js/common.js" type="text/javascript"></script>
        <script src="resources/js/ajouter.js" type="text/javascript"></script>
    </body>
</html>


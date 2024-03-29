<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:t="http://www.w3.org/1999/html">
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
        <div class="container">
            <div class="container position-fixed mt-2" style="z-index:9999">
                <div id="loading_message_container" class="container row">
                    <div id="loading_message_div" class="row align-items-center"></div>
                </div>
            </div>
            <h4 class="mt-2">Allez hop, il faut aller nager...</h4>

            <div class="text-center my-3">
                <img class="img-fluid" src="resources/images/phelps.jpg" />
            </div>

            <div class="alert alert-info my-2">
                Bienvenue sur le site des nageurs de public ! Pour rattraper un entrainement, simplement faire du zèle
                ou se moquer des copains qui ont dû morfler...
                <div class="mt-1">
                    Cliquez ci-dessous pour :
                    <ul class="mb-0">
                        <li>Trouver un entrainement</li>
                        <li>En ajouter un à la base de données</li>
                    </ul>
                </div>
            </div>

            <div id="menu_index" class="row my-4 justify-content-around">
                <div class="col-auto text-center">
                    <a href="search.jsp" class="img" title="Rechercher un entrainement">
                        <img width="150px" src="resources/images/search.png" />
                    </a>
                </div>
                <div class="col-auto text-center">
                    <a href="modification/ajouter.jsp" class="img" title="Ajouter des entrainements">
                        <img width="150px" src="resources/images/add.png" />
                    </a>
                </div>
            </div>

            <h4>
                Un entrainement aléatoire
                <button id="btn-load-another-random" class="ml-2 btn btn-primary">Un autre !</button>
            </h4>

            <div class="container">
                <div id="resAreaRandom" class="row"></div>
            </div>

            <h4>Les derniers entrainements ajoutés</h4>
            <div id="resArea" class="container"></div>

        </div>
        <div class="my_modal"></div>

        <!-- Scripts -->
        <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
        <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
        <script src="resources/js/rest.js" type="text/javascript"></script>
        <script src="resources/js/common.js" type="text/javascript"></script>
        <script src="resources/js/index.js" type="text/javascript"></script>
    </body>
</html>


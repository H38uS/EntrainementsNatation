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
        <link rel="stylesheet" type="text/css" href="resources/css/vendor/jquery-ui.min.css" />
        <link rel="stylesheet" type="text/css" href="resources/css/vendor/bootstrap.min-4.1.3.css" />
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
            <div id="menu_index" class="row my-4 justify-content-around">

            <h3>Doublons d'entrainement</h3>
            <div id="resDoublons" class="container"></div>

            <h3 class="mb-3">Gestion des utilisateurs</h3>
            <div id="resArea" class="container"></div>

            <h3 class="mb-3 d-block">Gestion des entraineurs</h3>
            <div class="container mb-2">
                <div class="form-inline">
                    <label for="coach-name">Ajouter un entraineur</label>
                    <input type="text" class="form-control mx-sm-3 mb-1" id="coach-name">
                    <button id="addCoach" type="submit" class="btn btn-primary">Ajouter</button>
                </div>
            </div>
            <div id="resCoaches" class="container mb-5"></div>

        </div>

        <!-- Scripts -->
        <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
        <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
        <script src="resources/js/rest.js" type="text/javascript"></script>
        <script src="resources/js/common.js" type="text/javascript"></script>
        <script src="resources/js/admin.js" type="text/javascript"></script>
    </body>
</html>
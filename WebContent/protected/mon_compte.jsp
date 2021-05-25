<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
        <div class="container mt-3">
            <div class="text-right d-block d-md-none">
                Connecté en tant que ${connected_user.email} - <a href="<c:url value="/logout" />">me deconnecter</a>.
            </div>
            <h3>Mes séances favorites</h3>
            <div class="form-row mt-2">
                <div class="col">
                    <input id="minsize" class="form-control" name="minsize" type="text" placeholder="Taille minimale" />
                </div>
                <div class="col">
                    <input id="maxsize" class="form-control" name="minsize" type="text" placeholder="Taille maximale" />
                </div>
            </div>
            <div class="form-inline justify-content-end">
                <label for="order" class="mt-2 mr-2">Trié par</label>
                <select id="order" class="mt-2 form-control mr-2" name="order">
                    <option value="date_seance desc">Les plus récents</option>
                    <option value="date_seance asc">Les plus anciens</option>
                    <option value="size asc">Taille de la séance</option>
                    <option value="size desc">Taille décroissante de la séance</option>
                </select>
                <button id="btn-rechercher" class="mt-2 btn btn-primary">Rechercher...</button>
            </div>
            <div id="resArea" class="container"></div>
            <div class="mb-2 text-right">
                <span id="info-nb-res" class="btn btn-info disabled mt-2"></span>
                <button id="btn-load-some-more" class="btn btn-primary mt-2 ml-2">Charger plus d'entrainements</button>
            </div>
        </div>
        <!-- Scripts -->
        <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
        <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
        <script src="resources/js/rest.js" type="text/javascript"></script>
        <script src="resources/js/common.js" type="text/javascript"></script>
        <script src="resources/js/mon_compte.js" type="text/javascript"></script>
    </body>
</html>
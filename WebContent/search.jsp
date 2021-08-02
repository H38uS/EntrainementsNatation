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
        <div class="container">
            <div class="container position-fixed mt-2" style="z-index:9999">
                <div id="loading_message_container" class="container row">
                    <div id="loading_message_div" class="row align-items-center"></div>
                </div>
            </div>
            <h4 class="mt-2">Allez hop, il faut aller nager...</h4>

            <div class="form-row mt-2">
                <div class="col">
                    <input id="minsize" class="form-control" name="minsize" type="text" placeholder="Taille minimale" />
                </div>
                <div class="col">
                    <input id="maxsize" class="form-control" name="minsize" type="text" placeholder="Taille maximale" />
                </div>
            </div>
            <div class="form-row mt-2">
                <div class="col">
                    <select id="from" class="form-control" name="from">
                        <option value="">Après le mois de ...</option>
                        <option value="1">Janvier</option>
                        <option value="2">Février</option>
                        <option value="3">Mars</option>
                        <option value="4">Avril</option>
                        <option value="5">Mai</option>
                        <option value="6">Juin</option>
                        <option value="7">Juillet</option>
                        <option value="8">Août</option>
                        <option value="9">Septembre</option>
                        <option value="10">Octobre</option>
                        <option value="11">Novembre</option>
                        <option value="12">Décembre</option>
                    </select>
                </div>
                <div class="col">
                    <select id="to" class="form-control" name="to">
                        <option value="">Avant le mois de ...</option>
                        <option value="1">Janvier</option>
                        <option value="2">Février</option>
                        <option value="3">Mars</option>
                        <option value="4">Avril</option>
                        <option value="5">Mai</option>
                        <option value="6">Juin</option>
                        <option value="7">Juillet</option>
                        <option value="8">Août</option>
                        <option value="9">Septembre</option>
                        <option value="10">Octobre</option>
                        <option value="11">Novembre</option>
                        <option value="12">Décembre</option>
                    </select>
                </div>
            </div>
            <div class="form-row mt-2">
                <div class="col">
                    <select id="coach" class="form-control" name="coach">
                        <option value="">Entraineur...</option>
                    </select>
                </div>
                <div class="col">
                    <select id="day" class="form-control" name="day">
                        <option value="">Jour de la semaine...</option>
                        <option value="1">Lundi</option>
                        <option value="2">Mardi</option>
                        <option value="3">Mercredi</option>
                        <option value="4">Jeudi</option>
                        <option value="5">Vendredi</option>
                        <option value="6">Samedi</option>
                        <option value="7">Dimanche</option>
                    </select>
                </div>
            </div>
            <div class="form-group form-check mt-2 mb-0 text-right">
                <input id="only-fav" class="form-check-input" type="checkbox" name="only-fav" />
                <label for="only-fav" class="form-check-label">Uniquement parmis mes favoris</label>
            </div>
            <div class="form-inline justify-content-end">
                <label for="order" class="mt-2 mr-2">Trié par</label>
                <select id="order" class="mt-2 form-control mr-2" name="order">
                    <option value="date_seance desc">Les plus récents</option>
                    <option value="date_seance asc">Les plus anciens</option>
                    <option value="size asc">Taille de la séance</option>
                    <option value="size desc">Taille décroissante de la séance</option>
                    <option value="rand()">Aléatoire</option>
                </select>
                <button id="btn-rechercher" class="mt-2 btn btn-primary">Rechercher...</button>
                <button id="btn-save-search" class="mt-2 ml-2 btn btn-secondary">
                    Sauver <span class="d-none d-lg-inline">les critères</span>
                </button>
                <button id="btn-delete-search" class="mt-2 ml-2 btn btn-secondary">Supprimer</button>
            </div>
            
            <div id="resArea" class="container"></div>
            <div class="mb-2 text-right">
                <span id="info-nb-res" class="btn btn-info disabled mt-2"></span>
                <button id="btn-load-some-more" class="btn btn-primary mt-2 ml-2">Charger plus d'entrainements</button>
            </div>
        </div>
        <div class="modal"></div>
        
        <!-- Scripts -->
        <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
        <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
        <script src="resources/js/rest.js" type="text/javascript"></script>
        <script src="resources/js/common.js" type="text/javascript"></script>
        <script src="resources/js/search.js" type="text/javascript"></script>
    </body>
</html>


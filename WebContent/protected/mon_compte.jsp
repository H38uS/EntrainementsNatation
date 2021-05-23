<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
        <div class="container mt-3">
            <div class="text-right">
                <a href="index.jsp" class="img" title="Retour à l'accueil">
                    <img width="50px" src="resources/images/home.png" />
                </a>
                <a href="modification/ajouter.html" class="img" title="Ajouter des entrainements">
                    <img width="50px" src="resources/images/add.png" />
                </a>
                <c:if test="${IS_ADMIN}">
                <a href="admin/admin.html" class="img">
                    <img width="50px" src="resources/images/admin.png" />
                </a>
                </c:if>
                <div class="text-right mt-2">
                    Connecté en tant que ${connected_user.email} - <a href="<c:url value="/logout" />">me deconnecter</a>.
                </div>
            </div>
            <h3>Mes séances favorites</h3>
            <div id="savedTrainingPlaceholder" class="container"></div>
        </div>
        <!-- Scripts -->
        <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
        <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
        <script src="resources/js/rest.js" type="text/javascript"></script>
        <script src="resources/js/common.js" type="text/javascript"></script>
        <script src="resources/js/mon_compte.js" type="text/javascript"></script>
    </body>
</html>
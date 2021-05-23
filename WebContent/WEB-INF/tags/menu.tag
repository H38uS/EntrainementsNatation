<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>

<header>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark py-0">
        <div class="container">
            <div class="row align-items-center justify-content-end mx-0 w-100">
                <div class="col-auto mr-0 mr-md-auto">
                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#menu_content" aria-controls="menu_content" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                </div>
                <c:if test="${connected_user}">
                    <div class="col-auto d-none d-md-flex text-light">
                        Bonjour, ${connected_user.name} -&nbsp;<a href="<c:url value="/logout" />">me deconnecter</a>
                    </div>
                </c:if>
                <div class="col-auto ml-auto ml-md-0 px-0 my-2">
                    <a href="protected/mes_notifications" class="btn btn-secondary ml-2">Mon compte</a>
                </div>
            </div>
        </div>
    </nav>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark py-0">
        <div class="container">
            <div class="row text-center w-100 mx-0">
                <div class="collapse navbar-collapse pb-2 pb-md-0" id="menu_content">
                    <ul class="navbar-nav mr-auto mt-lg-0 menu">
                        <li class="nav-item">
                            <a href="index.jsp" class="nav-link pl-md-0">Accueil</a>
                        </li>
                        <li class="nav-item">
                            <a href="modification/ajouter.html" class="nav-link">Ajouter</a>
                        </li>
                        <li class="nav-item">
                            <a href="search.html" class="nav-link">Rechercher</a>
                        </li>
                        <c:if test="${IS_ADMIN}">
                        <li class="nav-item">
                            <a href="admin/admin.html" class="nav-link">Administration</a>
                        </li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </nav>
</header>
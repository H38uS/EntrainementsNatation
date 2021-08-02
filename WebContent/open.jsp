<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
            <h4 class="pt-2">Séance numéro ${param["id"]}</h4>
            <div id="resArea"></div>
        </div>
        <div class="my_modal"></div>

        <!-- Scripts -->
        <script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
        <script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
        <script src="resources/js/rest.js" type="text/javascript"></script>
        <script src="resources/js/common.js" type="text/javascript"></script>
        <script type="text/javascript">
            startLoadingAnimation();
            doGet("public/service/training", { id: ${param["id"]}}, function(resp) {
                var col = getTrainingColDiv(resp.message, resp.canModify, resp.isAdmin);
                $("#resArea").append(col);
            });
        </script>
    </body>
</html>
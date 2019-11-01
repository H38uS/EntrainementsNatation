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
			<div>
				<div class="mt-2 text-right">
					<a href="index.html" class="img" title="Retour à l'accueil">
						<img width="50px" src="resources/images/home.png" />
					</a>
					<a href="search.html" class="img" title="Retour à la recherche d'entrainement">
						<img width="50px" src="resources/images/search.png" />
					</a>
				</div>
				<h4 class="">Ajouter un entrainement</h4>
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
				<div id="ajouter_feedback">
				</div>
				<div class="text-right">
					<a href="index.html" class="img" title="Retour à l'accueil">
						<img width="50px" src="resources/images/home.png" />
					</a>
					<a href="search.html" class="img" title="Retour à la recherche d'entrainement">
						<img width="50px" src="resources/images/search.png" />
					</a>
				</div>
			</div>
		</div>
		<div class="modal"></div>
		
		<!-- Scripts -->
		<script src="resources/js/vendor/jquery-3.2.1.min.js" type="text/javascript"></script>
		<script src="resources/js/vendor/bootstrap.bundle.min-4.1.3.js" type="text/javascript"></script>
		<script src="resources/js/common.js" type="text/javascript"></script>
		<script type="text/javascript">
			function computeTrainingSize() {
				$("#trainingSizeResult").hide();
				$.get(  "modification/service/trainingsize",
						{ training: $("#training").val() }
				).done(function (data) {
					var resp = JSON.parse(data);
					var text = "";
					if (resp.status === "OK") {
						text = resp.message;
					} else {
						text = "Une erreur est survenue: " + resp.message;
					}
					$("#trainingSizeResult").text(text).fadeIn();
				})
				.fail(displayError);
			}

			$("#training").change(computeTrainingSize);
			
			$.get("public/service/training", { id: $("#training_id").val()})
			.done(function (data) {
				var resp = JSON.parse(data);
				if (resp.status === 'OK') {
					
					
					$("#training").val(resp.message.text);
					computeTrainingSize();
					$("#size").val(resp.message.size);
					if (resp.message.dateSeance.length == 10) {
						var day = resp.message.dateSeance.substring(0, 2);
						var month = resp.message.dateSeance.substring(3, 5) - 1;
						var year = resp.message.dateSeance.substring(6, 10);
						var trainingDate = formatDate(new Date(year, month, day));
						$("#trainingdate").val(trainingDate);
					}

					$.get("public/service/coach")
					.done(function (data) {
						var coaches = JSON.parse(data);
						if (coaches.status === 'OK') {
							$.each(coaches.message, function(i, coach) {
								$('#coach').append('<option value="' + coach.name + '">' + coach.name + '</option>');
							});
							if (typeof resp.message.coach !== 'undefined') {
								$('#coach option[value=' + resp.message.coach.name + ']').prop('selected', true);
							}
						}
					})
					.fail(displayError);
					
					if (resp.message.isCourseSizeDefinedForSure) {
						if (resp.message.isLongCourse) {
							$('#radioGrandBac').prop('checked', true);
						} else {
							$('#radioPetitBac').prop('checked', true);
						}
					}
				} else {
					alert(resp.message);
				}
			})
			.fail(displayError);

			// Ajout d'un nouvel entrainement
			var feedbackTimeout = null;
			$("#modifier").click(function() {

				startLoadingAnimation();
				clearTimer(feedbackTimeout);
				$("#ajouter_feedback").hide();
				
				$.ajax({url: "modification/service/entrainement",
						type: "PUT",
						data: {
							id:				$("#training_id").val(),
							training: 		$("#training").val(),
							size: 			$("#size").val(),
							trainingdate:	$("#trainingdate").val(),
							coach:			$("#coach option:selected").val(),
							poolsize:		$('input[name=poolsize]:checked').val(),
						},
				}).done(function (data) {
					$("#ajouter_feedback").removeClass();
					var resp = JSON.parse(data);
					if (resp.status === "OK") {
						$("#ajouter_feedback").addClass("alert alert-success mt-2");
						$("#ajouter_feedback").text(resp.message);
						clearTimer(feedbackTimeout);
						feedbackTimeout = window.setTimeout(function () {
							$("#ajouter_feedback").fadeOut();
						}, 5000);
					} else {
						$("#ajouter_feedback").addClass("alert alert-danger mt-2");
						$("#ajouter_feedback").html(resp.message);
					}
					stopLoadingAnimation();
					$("#ajouter_feedback").fadeIn();
				})
				.fail(displayError);
			});
		</script>
	</body>
</html>
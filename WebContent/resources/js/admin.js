function grantModificationRole(userId) {

	startLoadingAnimation();
	$.post( "admin/service/admin",
			{
				userId : userId,
			}
	).done(function (data) {
		var rawData = JSON.parse(data);
		if (rawData.status !== "OK") {
			alert("Une erreur est survenue... Message : " + rawData.message);
		} else {
			loadUsers(false);
		}
	}).fail(displayError);
}

/** 
 * Loads admin data.
 */
function loadUsers(withAnimation) {

	if (withAnimation) {
		startLoadingAnimation();
	}
	
	$.get(  "admin/service/admin"
	).done(function (data) {
		
		$("#resArea").hide();
		var rawData = JSON.parse(data);
		var users = rawData.message;
		
		var content = $('<table class="table table-striped"></table>');
		var th = $('<thead><tr></tr></thead>');
		th.append('<th scope="col">User ID</th>');
		th.append('<th scope="col">User Email</th>');
		th.append('<th scope="col">Roles</th>');
		th.append('<th scope="col">User Creation Date</th>');
		th.append('<th scope="col">Actions</th>');
		content.append(th);
		
		var tbody = $("<tbody></tbody>");
		$.each(users, function(i, user) {
			
			var roles = [];
			$.each(user.roles, function(i, role) {
				roles.push(role.role);
			});
			var hasRoleModif = roles.includes("ROLE_MODIF") || roles.includes("ROLE_ADMIN");
			roles = roles.join(", ");
			
			var grantAction = $("<div></div>");
			if (!hasRoleModif) {
				grantAction.append("Grant Role Modif");
				grantAction.addClass("btn btn-primary");
				grantAction.click( () => grantModificationRole(user.id));
			}
			
			var tr = $("<tr></tr>");
			tr.append('<td>' + user.id + "</td>");
			tr.append("<td>" + user.email + "</td>");
			tr.append("<td>" + roles + "</td>");
			tr.append("<td>" + user.createdAt + "</td>");
			tr.append($('<td></td>').append(grantAction));
			tbody.append(tr);
		});
		content.append(tbody);
		
		$("#resArea").html(content).fadeIn('slow');
		stopLoadingAnimation();
	})
	.fail(displayError);
}

loadUsers(true);


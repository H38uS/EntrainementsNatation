function grantModificationRole(userId) {
    doPost( "admin/service/admin",
            {
                userId : userId,
            },
            function(resp) {
                loadUsers(false);
                actionDone("Grant fait avec succès.");
            }
    );
}

function loadDoublons() {
    doGet("admin/service/doublons", {}, function (resp) {
        var dates = resp.message;
        var res = $("#resDoublons");
        res.hide();
        $.each(dates, function(i, date) {
            res.append(`<div class="row alert alert-danger">Doublons détecté le ${date}...</div>`)
        });
        if (dates.length == 0) {
            res.append('<div class="row alert alert-success">Aucun doublons détecté !</div>')
        }
        res.fadeIn('slow');
    });
}

/** Loads admin data. */
function loadUsers(withAnimation) {

    if (withAnimation) {
        startLoadingAnimation();
    }

    doGet("admin/service/admin", {}, function(resp) {

        $("#resArea").hide();
        var users = resp.message;

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
    });
}

function loadCoachesInAdmin() {
    doGet("public/service/coach", {}, function (resp) {
        var resDiv = $("#resCoaches");
        resDiv.hide();

        var content = $("<div></div>");
        $.each(resp.message, function(i, coach) {
            var coachRow = $('<div class="row"></div>');
            var club = coach.club === undefined ? '- no club -' : coach.club;
            coachRow.append(`<div class="col-auto">${coach.id}</div>`);
            coachRow.append(`<div class="col col-lg-6 col-xl-3">${coach.name}</div>`);
            coachRow.append(`<div class="col-auto">${club}</div>`);
            content.append(coachRow);
        });

        resDiv.html(content).fadeIn('slow');
    });
}

function addCoach() {
    var name = $("#coach-name").val();
    doPost( "admin/service/coach",
            {
                coachName : name,
            },
            function(resp) {
                loadCoachesInAdmin();
                actionDone(name + " a bien été ajouté !");
            }
    );
}

$("#addCoach").click(addCoach);

loadDoublons();
loadUsers(true);
loadCoachesInAdmin();


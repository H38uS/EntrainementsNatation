function doGet(url, data = {}) {
    return $.get(url, data).fail(displayError);
}

function doPost(url, data = {}) {
    return $.post(url, data).fail(displayError);
}

function doDelete(url, data = {}) {
    return $.ajax({ url: url, type: "DELETE", data: data}).fail(displayError);
}

function doPut(url, data = {}) {
    return $.ajax({ url: url, type: "PUT", data: data}).fail(displayError);
}

function loadCoaches() {
    doGet("public/service/coach")
        .done(function (data) {
            var resp = JSON.parse(data);
            if (resp.status === 'OK') {
                $.each(resp.message, function(i, coach) {
                    $('#coach').append('<option value="' + coach.name + '">' + coach.name + '</option>');
                });
            }
        });
}
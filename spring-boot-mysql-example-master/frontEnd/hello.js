$(document).ready(function() {
    $.ajax({
        url: "http://localhost:8080/rest/users/id"
    }).then(function(data, status, jqxhr) {
       $('.greeting-id').append(data.id);
       $('.greeting-content').append(data.name);
       console.log(jqxhr);
    });
});
$(document).ready(function() {
  var zipCode;

  function ipLookUp() {
    $.ajax('http://ip-api.com/json')
      .then(
        function success(response) {
          console.log('User\'s Location Data is ', response);
          console.log('User\'s Country', response.country);
          zipCode = response.zip;
          $('.zipCode').append(zipCode);
          $('.zip').val(zipCode);
        },

        function fail(data, status) {
          console.log('Request failed.  Returned status of',
            status);
        }
      );
  }

  ipLookUp();

  function getWeather() {
    var email =   $('.zipCode').val();
    alert(zipCode +" " + email);
  }



  $.ajax({
    url: "http://localhost:8080/rest/users/id"
  }).then(function(data, status, jqxhr) {
    $('.greeting-id').append(data.id);
    $('.greeting-content').append(data.name);
    console.log(jqxhr);
  });

});
